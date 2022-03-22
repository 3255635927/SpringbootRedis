package com.boot.peterliu.redis.server.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.model.entity.Item;
import com.boot.peterliu.redis.model.mapper.ItemMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import com.boot.peterliu.redis.server.redis.StringRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/13 12:51
 * @Description: 解决缓存穿透、击穿、雪崩等问题的方案
 */
@Service
@Log4j2
public class CacheSchemeService {

    private static final Snowflake SNOWFLAKE = new Snowflake(3, 2);

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StringRedisService stringRedisService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //TODO:测试缓存穿透-查询商品
    public Item getItem(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            //判断缓存中是否存在这个key
            if (stringRedisService.ifExist(id.toString())) {
                String result = stringRedisService.get(id.toString()).toString();
//                log.info("缓存穿透-从缓存中取出来的value:{}", result);
                //判断缓存中的结果是否为空
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);//反序列化这个结果
                }
            } else {
                log.info("缓存穿透-从数据库中查询id:{}", id);
                //如果缓存中不存在这个key，就查询数据库中的实体，然后将实体存入缓存中
                item = itemMapper.selectByPrimaryKey(id);
                if (item != null) {
                    stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }


    //TODO:测试缓存穿透-查询商品-解决方法V2
    public Item getItemV2(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            //判断缓存中是否存在这个key
            if (stringRedisService.ifExist(id.toString())) {
                String result = stringRedisService.get(id.toString()).toString();
//                log.info("缓存穿透-从缓存中取出来的value:{}", result);
                //判断缓存中的结果是否为空
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);//反序列化这个结果
                }
            } else {
                log.info("缓存穿透-从数据库中查询id:{}", id);
                //如果缓存中不存在这个key，就查询数据库中的实体，然后将实体存入缓存中
                item = itemMapper.selectByPrimaryKey(id);
                //TODO:实际应用场景中，会使用限流(hystrix、guava提供的RateLimiter)解决某些问题
                if (item != null) {
                    stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                } else {
                    //如果查询为null，就将空字符串插入到缓存redis中
                    stringRedisService.put(id.toString(), "");
                    //给key设置1小时的过期时间
                    stringRedisService.expire(id.toString(), 3600L);
                }
            }
        }
        return item;
    }

    //TODO:测试缓存穿透-查询商品-解决方法V3-限流
    public Item getItemV3(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            //判断缓存中是否存在这个key
            if (stringRedisService.ifExist(id.toString())) {
                String result = stringRedisService.get(id.toString()).toString();
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);
                }
            } else {
                log.info("guava提供的RateLimiter,获取到令牌-缓存穿透，从数据库查询: id={}", id);
                item = itemMapper.selectByPrimaryKey(id);
                if (item != null) {
                    stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                } else {
                    stringRedisService.put(id.toString(), "");
                    //给key设置1小时的过期时间
                    stringRedisService.expire(id.toString(), 3600L);
                }
            }
        }
        return item;
    }


    //TODO:缓存击穿-查询商品
    public Item testCacheBreakdown(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            //判断缓存中是否存在这个key
            if (stringRedisService.ifExist(id.toString())) {
                String result = stringRedisService.get(id.toString()).toString();
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);
                }
            } else {
                //TODO:分布式锁的实现-同一时刻只能保证拥有该key的一个线程进入执行共享的业务代码。SETNX,EXPIRE,DELETE。利用redis单线程（原子性）
                //TODO:-IO多路复用(单核CPU,却支持多任务，多用户的使用，频繁切换很快，用户识别不了)
                String value = SNOWFLAKE.nextIdStr();//note:获取全局的分布式ID
                ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
                Boolean lock = valueOperations.setIfAbsent(Constant.RedisCacheBreakdownLockKey, value);//NOTE:利用互斥锁
                try {
                    if (lock) {
                        stringRedisTemplate.expire(Constant.RedisCacheBreakdownLockKey, 10L, TimeUnit.SECONDS);//NOTE:预防死锁

                        log.info("缓存击穿，从数据库查询: id={}", id);

                        item = itemMapper.selectByPrimaryKey(id);
                        if (item != null) {
                            stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                        } else {
                            stringRedisService.put(id.toString(), "");
                            //给key设置1小时的过期时间
                            stringRedisService.expire(id.toString(), 3600L);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    String currValue = valueOperations.get(Constant.RedisCacheBreakdownLockKey);
                    if (StrUtil.isNotBlank(currValue) && currValue.equals(value)) {
                        stringRedisTemplate.delete(Constant.RedisCacheBreakdownLockKey);
                    }
                }
            }
        }
        return item;
    }


}

package com.boot.peterliu.redis.server.service;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.model.entity.Item;
import com.boot.peterliu.redis.model.mapper.ItemMapper;
import com.boot.peterliu.redis.server.redis.StringRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Log4j2
public class StringService {
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StringRedisService stringRedisService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 添加商品
     *
     * @param item
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)//发生异常时回退
    public Integer addItem(Item item) throws Exception {
        item.setCreateTime(new Date());
        item.setId(null);//设置为Null使得数据库id自增
        itemMapper.insertSelective(item);
        //写入数据库后，id通过自增将不为null，这时数据库存储成功，接下来执行写入redis缓存的操作
        Integer id = item.getId();
        //保证缓存和数据库双写的一致性
        if (id > 0) {
            stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));//存储的value必须是序列化对象
        }
        return id;
    }

    /**
     * 获取商品
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Item getItem(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            //判断缓存中是否存在这个key
            if (stringRedisService.ifExist(id.toString())) {
                String result = stringRedisService.get(id.toString()).toString();
                log.info("String数据类型，从缓存中取出来的value:{}", result);
                //判断缓存中的结果是否为空
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);//反序列化这个结果
                }
            } else {
                log.info("String数据类型，从数据库中查询id:{}",id);
                //如果缓存中不存在这个key，就查询数据库中的实体，然后将实体存入缓存中
                item = itemMapper.selectByPrimaryKey(id);
                if (item != null) {
                    stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }


}

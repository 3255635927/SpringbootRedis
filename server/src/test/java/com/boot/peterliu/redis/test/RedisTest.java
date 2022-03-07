package com.boot.peterliu.redis.test;

import com.boot.peterliu.redis.server.MainApplication;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.util.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 字符串的测试
     */
    @Test
    public void method1() {
        log.info("----此处打印字符串----");
        final String key = "SpringbootRedis:Order:1001";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, 1001L);
        log.info("当前key={} 所对应的value={}", key, valueOperations.get(key));

        valueOperations.increment(key, 1001L);
        log.info("当前key={} 所对应的value={}", key, valueOperations.get(key));

    }

    /**
     * 字符串列表的测试
     */
    @Test
    public void method2() {
        log.info("开始列表list测试");
        final String key = "SpringBootRedis:List:10010";
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        redisTemplate.delete(key);//清空key

        //TODO: 列表插入顺序为e,d,c,b,a
        List<String> list = Lists.newArrayList("c", "d", "e");
        listOperations.leftPush(key, "a");
        listOperations.leftPush(key, "b");

        listOperations.leftPushAll(key, list);

        log.info("当前列表元素个数：{}", listOperations.size(key));
        log.info("当前列表元素有：{}", listOperations.range(key, 0L, 10L));

        log.info("当前列表中下标为0的元素是:{}", listOperations.index(key, 0L));
        log.info("当前列表中下标为4的元素是:{}", listOperations.index(key, 4L));
        log.info("当前列表中下标为10的元素是:{}", listOperations.index(key, 10L));

        log.info("当前列表中从右边弹出来的元素：{}", listOperations.rightPop(key));

        listOperations.set(key, 0L, "99");
        log.info("当前列表中下标为0的元素：{}", listOperations.index(key, 0L));
        log.info("当前列表元素有：{}", listOperations.range(key, 0L, 10L));

        listOperations.remove(key,0,"99");
        log.info("当前列表元素有：{}", listOperations.range(key, 0L, 10L));

    }

    /**
     * Set数据结构单元测试
     */
    @Test
    public void test3(){
        log.info("开始集合Set的测试");
        final String key1="SpringBootRedis:Set:1001";
        final String key2="SpringBootRedis:Set:1002";
        redisTemplate.delete(key1);
        redisTemplate.delete(key2);

        SetOperations setOperations = redisTemplate.opsForSet();

        setOperations.add(key1,new String[]{"a","b","c"});
        setOperations.add(key2,new String[]{"b","e","f"});

        log.info("集合key1的元素:{} ",setOperations.members(key1));
        log.info("集合key2的元素:{} ",setOperations.members(key2));

        log.info("集合key1中随机取出的1个元素:{} ",setOperations.randomMember(key1));
        log.info("集合key2中随机取出的n个元素:{} ",setOperations.randomMembers(key2,2L));

        log.info("集合key1中元素个数:{} ",setOperations.size(key1));
        log.info("集合key2中元素个数:{} ",setOperations.size(key2));

        log.info("元素e是否是key1集合中的:{} ",setOperations.isMember(key1,"e"));
        log.info("元素f是否是key2集合中的:{} ",setOperations.isMember(key2,"f"));

        log.info("集合key1和集合key2的差集元素:{}",setOperations.difference(key1,key2));
        log.info("集合key1和集合key2的交集元素:{}",setOperations.intersect(key1,key2));
        log.info("集合key1和集合key2的并集元素:{}",setOperations.union(key1,key2));

        log.info("从集合key1中弹出的一个随机元素:{}",setOperations.pop(key1,1L));
        log.info("集合key1的元素:{} ",setOperations.members(key1));

        log.info("从集合key2中移除元素e:{} ",setOperations.remove(key2,"e"));

    }

    /**
     * 测试有序集合SortedSet
     */
    @Test
    public void method4(){
        log.info("开始有序集合SortedSet测试");

        final String key="SpringBootRedis:SortedSet:1001";
        redisTemplate.delete(key);

        ZSetOperations<String,String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key,"a",5.0);
        zSetOperations.add(key,"b",2.0);
        zSetOperations.add(key,"c",1.0);
        zSetOperations.add(key,"d",7.0);

        log.info("有序集合SortedSet-成员数:{}",zSetOperations.size(key));
        log.info("有序集合SortedSet-元素正序排列:{}",zSetOperations.range(key,0L,zSetOperations.size(key)));
        log.info("有序集合SortedSet-元素倒序排列:{}",zSetOperations.reverseRange(key,0L,zSetOperations.size(key)));

        log.info("有序集合SortedSet-获取成员a得分:{}",zSetOperations.score(key,"a"));
        log.info("有序集合SortedSet-获取成员b得分:{}",zSetOperations.score(key,"b"));

        log.info("有序集合SortedSet-正序中c的排名第:{}",zSetOperations.rank(key,"c"));
        log.info("有序集合SortedSet-倒序中c的排名第:{}",zSetOperations.reverseRank(key,"c"));

        zSetOperations.incrementScore(key,"a",10.0);
        log.info("有序集合SortedSet-元素正序排列:{}",zSetOperations.range(key,0L,zSetOperations.size(key)));

        zSetOperations.remove(key,"c");
        log.info("有序集合SortedSet-元素正序排列:{}",zSetOperations.range(key,0L,zSetOperations.size(key)));

        log.info("有序集合SortedSet-取出10分以内的元素:{}",zSetOperations.rangeByScore(key,0L,10L));
    }

    //TODO:测试HASH数据类型的相关API
    @Test
    public void method5(){
        log.info("测试HASH数据类型的相关API~开始");
        final String key="SpringBootRedis:Hash:Key:V1";
        redisTemplate.delete(key);
        //对key，field以及value都声明为字符串
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(key,"10010","zhangsan");
        hashOperations.put(key,"10011","lisi");

        Map<String,String> resMap = Maps.newHashMap();
        resMap.put("10012","wangwu");
        resMap.put("10013","zhaoliu");
        hashOperations.putAll(key,resMap);

        log.info("哈希HASH~获取所有列表元素:{}",hashOperations.entries(key));
        log.info("哈希HASH~获取field为10011的元素:{}",hashOperations.get(key,"10011"));
        log.info("哈希HASH~获取所有元素的field列表:{}",hashOperations.keys(key));

        log.info("哈希HASH~判断field为10013的元素是否存在:{}",hashOperations.hasKey(key,"10013"));
        log.info("哈希HASH~判断field为10014的元素是否存在:{}",hashOperations.hasKey(key,"10014"));

        hashOperations.putIfAbsent(key,"10014","peterliu");
        log.info("哈希HASH~获取存在的列表元素:{}",hashOperations.entries(key));

        log.info("哈希HASH~删除元素中field为10010和10011的元素:{}",hashOperations.delete(key,"10010","10011"));
        log.info("哈希HASH~获取存在的列表元素:{}",hashOperations.entries(key));

        log.info("哈希HASH~获取元素个数:{}",hashOperations.size(key));
    }









}






















package com.boot.peterliu.redis.test;

import com.boot.peterliu.redis.server.MainApplication;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

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


}








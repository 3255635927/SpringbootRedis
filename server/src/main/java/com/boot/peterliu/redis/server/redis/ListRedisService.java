package com.boot.peterliu.redis.server.redis;

import com.boot.peterliu.redis.model.entity.Product;
import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ListRedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    //往缓存中添加信息
    public void pushProduct(Product product) throws Exception {
        ListOperations<String, Product> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(Constant.RedisListPrefix+product.getUserId(),product);
    }







}


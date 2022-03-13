package com.boot.peterliu.redis.server.redis;

import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 单独设计一个缓存业务，暂时不与其他业务耦合在一起
 */

@Log4j2
@Service
public class StringRedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations getValueOperations(){
        return stringRedisTemplate.opsForValue();
    }

    /**
     * 将数据存放到缓存中
     * @param key
     * @param value
     * @throws Exception
     */
    public void put(final String key,final String value) throws Exception{
        getValueOperations().set(Constant.RedisStringPrefix+key,value);
    }

    /**
     * 从缓存中得到key
     * @param key
     * @return
     * @throws Exception
     */
    public Object get(final String key) throws Exception{
        return getValueOperations().get(Constant.RedisStringPrefix+key);
    }

    /**
     * 判断缓存中是否存在这个key
     * @param key
     * @return
     * @throws Exception
     */
    public Boolean ifExist(final String key) throws Exception{
        return stringRedisTemplate.hasKey(Constant.RedisStringPrefix+key);
    }

    /**
     * 给key设置过期时间
     * @param key
     * @param expireTime
     */
    public void expire(final String key,final Long expireTime){
        stringRedisTemplate.expire(key,expireTime, TimeUnit.SECONDS);
    }

}

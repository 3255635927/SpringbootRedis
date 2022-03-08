package com.boot.peterliu.redis.server.redis;

import com.boot.peterliu.redis.model.entity.SysConfig;
import com.boot.peterliu.redis.model.mapper.SysConfigMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/7 18:04
 * @Description: 哈希redis相关业务
 */
@Log4j2
@Service
public class HashRedisService {
    @Autowired
    private SysConfigMapper configMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //TODO；实时获取所有有效的数据字典列表~转化为map~存入hash缓存中
    public void cacheConfigMap() {
        try {
            List<SysConfig> sysConfigs = configMapper.selectActiveConfigs();
            if (sysConfigs != null && !sysConfigs.isEmpty()) {
                Map<String, List<SysConfig>> resMap = Maps.newHashMap();
                //TODO:遍历所有的数据字典~转化为所有的hash存储的map
                sysConfigs.stream().forEach(config -> {
                    List<SysConfig> list = resMap.get(config.getType());//通过类型找到对应的结果集
                    if (list == null || list.isEmpty()) {
                        //如果为空的话，就定义一个空的有序链表
                        list = Lists.newLinkedList();
                    }
                    //不存在的话，添加新的进去
                    list.add(config);
                    //每次遍历后都要存放到map中去
                    resMap.put(config.getType(), list);//大类对应的小选项集合
                });
                //TODO:存储到hash缓存中
                HashOperations<String, String, List<SysConfig>> hashOperations = redisTemplate.opsForHash();
                hashOperations.putAll(Constant.RedisHashKey, resMap);
            }
        } catch (Exception e) {
            log.info("实时获取所有有效的数据字典列表~发生异常:{}", e.fillInStackTrace());
        }
    }

    //TODO:从缓存中获取所有数据字典配置列表
    public Map<String, List<SysConfig>> getWholeCacheConfigs() {
        Map<String, List<SysConfig>> resMap = Maps.newHashMap();
        try {
            HashOperations<String, String, List<SysConfig>> hashOperations = redisTemplate.opsForHash();
            resMap = hashOperations.entries(Constant.RedisHashKey);
        } catch (Exception e) {
            log.info("从缓存中获取所有数据字典配置列表~发生异常:{}", e.fillInStackTrace());
        }
        return resMap;
    }

    //TODO:获取缓存中某一类型下所有的列表数据
    public List<SysConfig> getCacheConfigsByType(final String type) {
        List<SysConfig> list = Lists.newLinkedList();
        try {
            HashOperations<String, String, List<SysConfig>> hashOperations = redisTemplate.opsForHash();
            list=hashOperations.get(Constant.RedisHashKey,type);
        } catch (Exception e) {
            log.info("获取缓存中某一类型下所有的列表数据~发生异常:{}", e.fillInStackTrace());
        }
        return list;
    }

}

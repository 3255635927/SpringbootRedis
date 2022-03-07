package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.SysConfig;
import com.boot.peterliu.redis.model.mapper.SysConfigMapper;
import com.boot.peterliu.redis.server.redis.HashRedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/7 18:02
 * @Description:
 */
@Service
@Log4j2
public class HashService {
    @Autowired
    private SysConfigMapper configMapper;
    @Autowired
    private HashRedisService hashRedisService;

    //TODO:添加数据字典以及对应的选项(code-value)
    @Transactional(rollbackFor = Exception.class)
    public Integer addSysConfig(SysConfig config) throws Exception {
        int res = configMapper.insertSelective(config);
        if (res > 0) {
            //TODO:实时触发数据字典的哈希存储
            hashRedisService.cacheConfigMap();
        }
        return config.getId();
    }


}








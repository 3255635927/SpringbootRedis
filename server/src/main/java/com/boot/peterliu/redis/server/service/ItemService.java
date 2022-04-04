package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Item;
import com.boot.peterliu.redis.model.mapper.ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: PeterLiu
 * @Date: 2022/4/4 11:33
 * @Description:
 */
@Slf4j
@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    //TODO:这里测试，如果不注释掉类转换异常，因为devtools为了实现重新加载class自己实现了一个类加载器，所以会导致类强转异常
    //TODO:value必填，key支持springEL表达式
    //TODO:缓存存在时，则直接走缓存，否则，走数据库
    @Cacheable(value = "SpringBootRedis:item", key = "#id", unless = "#result == null")
    public Item getItemInfo(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        log.info(" @Cacheable走数据库查询:{}", item);
        return item;
    }

    ///TODO:不管缓存中存不存在，都会put到缓存中
    @CachePut(value = "SpringBootRedis:item", key = "#id")
    public Item getItemInfoV2(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        item.setCode(UUID.randomUUID().toString());
        log.info("@CachePut走数据库查询:{}", item);
        return item;
    }

    //TODO:失效\删除缓存
    @CacheEvict(value = "SpringBootRedis:item", key = "#id")
    public Boolean deleteItem(Integer id) {
        int res = itemMapper.deleteByPrimaryKey(id);
        log.info("@CacheEvict删除缓存,id={}", id);
        return res > 0 ? true : false;
    }

}

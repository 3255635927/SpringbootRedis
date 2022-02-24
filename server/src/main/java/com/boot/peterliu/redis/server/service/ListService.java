package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Product;
import com.boot.peterliu.redis.model.mapper.ProductMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import com.boot.peterliu.redis.server.redis.ListRedisService;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class ListService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ListRedisService listRedisService;
    @Autowired
    private RedisTemplate redisTemplate;

    //添加商户商品
    @Transactional(rollbackFor = Exception.class)//保证数据的一致性
    public Integer addProduct(Product product) throws Exception{
        if(product!=null){
            product.setId(null);
            productMapper.insertSelective(product);
            Integer id = product.getId();
            //存入数据库之后，得到自增后的商品id
            if(id>0){
                listRedisService.pushProduct(product);//将商品加入到缓存中
            }
            return id;
        }
        return -1;
    }

    //获取历史发布的商品列表
    public List<Product> getHistoryProduct(final Integer userId) throws Exception{
        List<Product> list=Lists.newLinkedList();
        ListOperations<String,Product> listOperations = redisTemplate.opsForList();
        final String key= Constant.RedisListPrefix+userId;
        //TODO:倒序 userId->10012;  Nacos注册中心,Ribbon负载均衡,Dubbo技术入门
//        list=listOperations.range(key,0L,listOperations.size(key));
//        log.info("倒序:{}",list);

        //TODO:顺序 userId->10012; Dubbo技术入门,Ribbon负载均衡,Nacos注册中心
//        Collections.reverse(list);//方式一
//        log.info("顺序:{}",list);

        //TODO:使用弹出的方式移除
        Product product =listOperations.rightPop(key);
        while(product!=null){
            list.add(product);
            product=listOperations.rightPop(key);
        }
        return list;
    }











}

package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Product;
import com.boot.peterliu.redis.model.mapper.ProductMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class ListService {
    @Autowired
    private ProductMapper productMapper;

    //添加商户商品
    @Transactional(rollbackFor = Exception.class)//保证数据的一致性
    public void addProduct(Product product) throws Exception{
        if(product!=null){

        }
    }


}

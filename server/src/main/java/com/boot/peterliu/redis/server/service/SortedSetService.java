package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.PhoneFare;
import com.boot.peterliu.redis.model.mapper.PhoneFareMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/28 21:09
 * @Description:有序集合SortedSet在业务层的实现
 */
@Service
@Log4j2
public class SortedSetService {
    @Autowired
    private PhoneFareMapper fareMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //TODO:新增手机话费充值记录
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecord(PhoneFare fare) throws Exception{
        log.info("有序集合SortedSet-话费充值记录-新增:{}",fare);
        int res = fareMapper.insertSelective(fare);
        if(res>0){
            ZSetOperations<String,PhoneFare> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add(Constant.RedisSortedSetKey1,fare,fare.getFare().doubleValue());
        }
        return fare.getId();
    }

    //TODO:获取充值排行榜
    public Set<PhoneFare> getSortedFares(final Boolean isAsc){
        final String key=Constant.RedisSortedSetKey1;
        ZSetOperations<String,PhoneFare> zSetOperations = redisTemplate.opsForZSet();
        Long size = zSetOperations.size(key);
//        zSetOperations.rangeByScore(key,50L,130L);//尝试取到50到130之间的排行榜
        //如果是递增，就正序输出结果；否则，倒序排列输出
        return isAsc ? zSetOperations.range(key,0L,size) : zSetOperations.reverseRange(key,0L,size);
    }



}

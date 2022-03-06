package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.dto.FareDto;
import com.boot.peterliu.redis.model.entity.PhoneFare;
import com.boot.peterliu.redis.model.mapper.PhoneFareMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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

    //TODO:新增手机话费充值记录 V1
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

    //TODO:新增手机话费充值记录 V2
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecordV2(PhoneFare fare) throws Exception{
        log.info("有序集合SortedSet-话费充值记录-新增:{}",fare);
        int res = fareMapper.insertSelective(fare);
        if(res>0){
            FareDto fareDto = new FareDto(fare.getPhone());
            ZSetOperations<String,FareDto> zSetOperations = redisTemplate.opsForZSet();
            Double oldFare = zSetOperations.score(Constant.RedisSortedSetKey2, fareDto);
            if(oldFare!=null){
                //TODO:如果存在，则在原来的基础上继续增加金额
                zSetOperations.incrementScore(Constant.RedisSortedSetKey2,fareDto,fare.getFare().doubleValue());
            }else{
                //TODO:如果不存在，表示只充过一次话费，对应则新增该手机号对应的话费金额
                zSetOperations.add(Constant.RedisSortedSetKey2,fareDto,fare.getFare().doubleValue());
            }
        }
        return fare.getId();
    }

    //TODO:获取充值排行榜 V2
    public List<PhoneFare> getSortedFaresV2(){
        List<PhoneFare> list= Lists.newLinkedList();//使用list返回有序集合

        final String key=Constant.RedisSortedSetKey2;
        ZSetOperations<String,FareDto> zSetOperations = redisTemplate.opsForZSet();
        Long size = zSetOperations.size(key);

        Set<ZSetOperations.TypedTuple<FareDto>> itemSet = zSetOperations.reverseRangeWithScores(key, 0L, size);
        if(itemSet!=null && !itemSet.isEmpty()){
            itemSet.stream().forEach(tuple -> {
                PhoneFare fare = new PhoneFare();
                fare.setPhone(tuple.getValue().getPhone());
                fare.setFare(BigDecimal.valueOf(tuple.getScore()));
                list.add(fare);
            });
        }
        return list;
    }


}

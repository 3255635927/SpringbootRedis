package com.boot.peterliu.redis.server.scheduler;

import com.boot.peterliu.redis.model.dto.FareDto;
import com.boot.peterliu.redis.model.entity.PhoneFare;
import com.boot.peterliu.redis.model.mapper.PhoneFareMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/6 22:33
 * @Description: 定时任务：定期清除缓存中的数据，重新把MySQL数据库中的数据写入缓存,并排序
 * 保证mysql数据库与缓存数据的一致性
 */
@Component
@Log4j2
public class PhoneFareScheduler {
    @Autowired
    private PhoneFareMapper fareMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //TODO:每20秒执行一次(实际场景中并没有这么频繁，可以增加时长，如10小时重新写入)
//    @Scheduled(cron = "0/20 * * * * ?") //暂时注释掉，需要时开启即可
    public void sortFareScheduler(){
        log.info("话费充值排行~保证mysql数据库与缓存数据的一致性-定时任务");
        this.cacheSortResult();
    }

//    @Async("threadPoolTaskExecutor")  //暂时注释掉，需要时开启即可
    protected void cacheSortResult(){
        try{
            ZSetOperations<String, FareDto> zSetOperations = redisTemplate.opsForZSet();
            List<PhoneFare> fareList = fareMapper.getAllSortFares();
            if(fareList!=null && !fareList.isEmpty()){
                //数据库中的数据不为空的话，定时删除缓存中的数据，并重新把数据库数据写入缓存
                redisTemplate.delete(Constant.RedisSortedSetKey2);
                fareList.stream().forEach(fare -> {
                    FareDto fareDto = new FareDto(fare.getPhone());
                    zSetOperations.add(Constant.RedisSortedSetKey2,fareDto,fare.getFare().doubleValue());
                });
            }
        }catch (Exception e){
            log.error("保证mysql数据库与缓存数据的一致性-定时任务-发生异常：",e.fillInStackTrace());
        }
    }
}

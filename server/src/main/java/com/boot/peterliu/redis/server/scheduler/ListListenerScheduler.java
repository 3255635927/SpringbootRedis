package com.boot.peterliu.redis.server.scheduler;

import com.boot.peterliu.redis.model.entity.Notice;
import com.boot.peterliu.redis.model.entity.User;
import com.boot.peterliu.redis.model.mapper.UserMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import com.boot.peterliu.redis.server.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/25 12:26
 * @Description:Redis列表-队列的消费者监听器
 */
@Component
@EnableScheduling
@Log4j2
public class ListListenerScheduler {

    private static final String listenKey= Constant.RedisListNoticeKey;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailService emailService;

    //TODO: 近实时的定时任务检测,下面的意思是：从0秒开始，每隔10秒触发一次
    @Scheduled(cron = "0/10 * * * * ?")
    public void schedulerListenNotice() {
        log.info("定时任务调度队列监听、检测通告消息，监听list中的数据");
        ListOperations<String,Notice> listOperations = redisTemplate.opsForList();
        Notice notice= listOperations.rightPop(listenKey);
        while(notice!=null){
            //TODO:发送通知到所有的商户邮箱
            this.notifyTheUser(notice);
            notice=listOperations.rightPop(listenKey);
        }
    }

    //TODO:发送通知给到不同的商户
    private void notifyTheUser(Notice notice){
        if(notice!=null){
            List<User> users = userMapper.selectList();
            //TODO:java8 stream写法
            if(users!=null && !users.isEmpty()){
                users.stream().forEach(user -> emailService.emailNotificationToUsers(notice,user));
            }



        }
    }








}























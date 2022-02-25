package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Notice;
import com.boot.peterliu.redis.model.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/25 15:35
 * @Description: 邮箱服务
 */
@Service
@Log4j2
public class EmailService {

    //TODO:给指定的用户发送通告
    public void emailNotificationToUsers(Notice notice, User user){
        log.info("给指定用户:{} 发送通告:{}",user,notice);
    }


}














package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Notice;
import com.boot.peterliu.redis.model.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/25 15:35
 * @Description: 邮箱服务
 */
@Service
@Log4j2
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment environment;

    //TODO:给指定的用户发送通告
    public void emailNotificationToUsers(Notice notice, User user) {
        log.info("给指定用户:{} 发送通告:{}", user, notice);
        this.sendSimpleEmail(notice.getTitle(), notice.getContent(), user.getEmail());
    }

    //TODO:发送简单的邮件消息
    public void sendSimpleEmail(final String subject, final String content, final String... toWhom) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(subject);
            message.setText(content);
            message.setFrom(environment.getProperty("mail.send.from"));//发送者
            message.setTo(toWhom);//接收者

            mailSender.send(message);
            log.info("发送邮箱完毕");
        } catch (Exception e) {
            log.info("发送邮件消息-发生异常：{}",e.fillInStackTrace());
        }
    }


}














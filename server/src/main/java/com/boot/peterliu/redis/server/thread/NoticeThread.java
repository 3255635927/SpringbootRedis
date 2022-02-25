package com.boot.peterliu.redis.server.thread;

import com.boot.peterliu.redis.model.entity.Notice;
import com.boot.peterliu.redis.model.entity.User;
import com.boot.peterliu.redis.server.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/25 17:34
 * @Description:发送通告给商户的线程
 */
public class NoticeThread implements Callable<Boolean> {

    private User user;
    private Notice notice;

    private EmailService emailService;

    public NoticeThread(User user, Notice notice,EmailService emailService) {
        this.user = user;
        this.notice = notice;
        this.emailService = emailService;
    }

    @Override
    public Boolean call() throws Exception {
        emailService.emailNotificationToUsers(notice,user);
        return true;
    }
}

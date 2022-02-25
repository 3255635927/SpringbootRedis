package com.boot.peterliu.redis.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/25 16:28
 * @Description: 线程池配置
 */
public class ThreadConfig {

    @Bean("threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 10L, TimeUnit.SECONDS, (BlockingQueue<Runnable>) new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }


}


package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.model.entity.Problem;
import com.boot.peterliu.redis.model.mapper.ProblemMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/27 20:25
 * @Description: 随机问题业务
 */
@Log4j2
@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //TODO:项目启动时拉取出数据库中的问题库，并将问题塞入缓存中
    @PostConstruct   //@PostConstruct该注解被用来修饰一个非静态的void（）方法。被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
    public void init() {
        initDataIntoCache();
    }

    //TODO:拉取出数据库中的问题库，并将问题塞入缓存中
    private void initDataIntoCache() {
        try {
            Set<Problem> problems = problemMapper.getAll();
            SetOperations<String, Problem> setOperations = redisTemplate.opsForSet();
            if (problems != null && !problems.isEmpty()) {
                problems.stream().forEach(problem -> setOperations.add(Constant.RedisProblemSetKey, problem));
            }
        } catch (Exception e) {
            log.error("项目启动时拉取出数据库中的问题库，并将问题塞入缓存中~发生异常:{}", e.getMessage());
        }
    }

    //TODO:从缓存中获取随机的问题
    public Problem getRandomEntity() {
        Problem problem = null;
        try {
            SetOperations<String, Problem> setOperations = redisTemplate.opsForSet();
            Long size = setOperations.size(Constant.RedisProblemSetKey);
            if (size > 0) {
                problem = setOperations.pop(Constant.RedisProblemSetKey);
            } else {
                this.initDataIntoCache();
                problem = setOperations.pop(Constant.RedisProblemSetKey);
            }
        } catch (Exception e) {
            log.error("从缓存中获取随机的问题~发生异常:{}", e.getMessage());
        }
        return problem;
    }


}

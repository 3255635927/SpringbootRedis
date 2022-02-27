package com.boot.peterliu.redis.server.service;

import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.Problem;
import com.boot.peterliu.redis.model.entity.User;
import com.boot.peterliu.redis.model.mapper.UserMapper;
import com.boot.peterliu.redis.server.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/26 13:51
 * @Description:
 */
@Service
@Log4j2
public class SetService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProblemService problemService;

    //TODO:用户注册
    @Transactional(rollbackFor = Exception.class)
    public Integer registerUser(User user) throws Exception {
        if (this.ifExist(user.getEmail())) {
            throw new RuntimeException(StatusCode.EmailAlreadyExists.getMsg());
        }
        int res = userMapper.insertSelective(user);
        if (res > 0) {
            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
            setOperations.add(Constant.RedisSetKey, user.getEmail());
        }
        return user.getId();
    }

    //TODO:判断缓存中是否已经存在该邮箱
    private Boolean ifExist(final String email) throws Exception {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        //TODO:写法一
//        Boolean flag = setOperations.isMember(Constant.RedisSetKey, email);
//        if (flag) {
//            return true;
//        } else {
//            User user = userMapper.selectByEmail(email);
//            if (user != null) {
//                //如果数据库中不存在，则添加该邮箱到缓存中
//                setOperations.add(Constant.RedisSetKey, user.getEmail());
//                return true;
//            } else {
//                return false;
//            }
//        }

        //TODO:写法二
        Long size = setOperations.size(Constant.RedisSetKey);
        if (size > 0 && setOperations.isMember(Constant.RedisSetKey, email)) {
            return true;
        } else {
            User user = userMapper.selectByEmail(email);
            if (user != null) {
                //如果数据库中不存在，则添加该邮箱到缓存中
                setOperations.add(Constant.RedisSetKey, user.getEmail());
                return true;
            } else {
                return false;
            }
        }
    }

    //TODO:取出缓存中已注册的邮箱列表
    public Set<String> getEmails()throws Exception{
        SetOperations<String,String> setOperations = redisTemplate.opsForSet();
        return setOperations.members(Constant.RedisSetKey);
    }

    //TODO:从存在于缓存的问题库中随机弹出一个问题
    public Problem getRandomProblem() throws Exception{
       return problemService.getRandomEntity();
    }

    //TODO:从缓存中随机、乱序地取出固定数量的试题集合
    public Set<Problem> getRandomQuestions(Integer recordCount){
        return problemService.getRandomQuestionList(recordCount);
    }






}









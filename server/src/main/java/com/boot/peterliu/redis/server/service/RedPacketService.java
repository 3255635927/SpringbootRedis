package com.boot.peterliu.redis.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import com.boot.peterliu.redis.model.dto.RedPacketDto;
import com.boot.peterliu.redis.model.entity.RedDetail;
import com.boot.peterliu.redis.model.entity.RedRecord;
import com.boot.peterliu.redis.model.entity.RedRobRecord;
import com.boot.peterliu.redis.model.mapper.RedDetailMapper;
import com.boot.peterliu.redis.model.mapper.RedRecordMapper;
import com.boot.peterliu.redis.model.mapper.RedRobRecordMapper;
import com.boot.peterliu.redis.server.utils.RedPacketUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/23 15:12
 * @Description:用于缓存的红包业务
 */
@Log4j2
@Service
public class RedPacketService {
    @Autowired
    private RedRecordMapper redRecordMapper;
    @Autowired
    private RedDetailMapper detailMapper;
    @Autowired
    private RedRobRecordMapper recordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //%s是占位符，第一个表示用户id，第二个表示一个字符串串
    public static final String RedPacketKey = "SpringbootRedis:RedPacket:%s:%s";

    private static final Snowflake SNOWFLAKE = new Snowflake(3, 2);

    //note:发红包逻辑
    @Transactional(rollbackFor = Exception.class)
    public String distributeRedPacket(RedPacketDto redPacketDto) throws Exception {
        if (redPacketDto.getAmount() > 0 && redPacketDto.getPeople() > 0) {
            //TODO:生成红包全局唯一标识串
            String packetKey = String.format(RedPacketKey, redPacketDto.getUserId(), SNOWFLAKE.nextIdStr());

            //TODO:随机生成红包金额列表
            List<Integer> list = RedPacketUtil.divideAmount(redPacketDto.getAmount(), redPacketDto.getPeople());

            //TODO:写入数据库
            recordRedPacket(redPacketDto, list, packetKey);
            //TODO:写入redis缓存 (总个数，随机金额列表)
            redisTemplate.opsForValue().set(packetKey + ":total", redPacketDto.getPeople());
            redisTemplate.opsForList().leftPushAll(packetKey, list);
            return packetKey;
        }
        return null;
    }

    /**
     * 将分发的红包数据写入数据库
     * @param packetDto
     * @param list      每个红包金额组成的list
     * @param packetKey
     */
    private void recordRedPacket(RedPacketDto packetDto, List<Integer> list, final String packetKey) {
        RedRecord entity = new RedRecord();
        entity.setAmount(BigDecimal.valueOf(packetDto.getAmount()));
        entity.setCreateTime(DateTime.now().toJdkDate());
        entity.setUserId(packetDto.getUserId());
        entity.setRedPacket(packetKey);
        entity.setTotal(packetDto.getPeople());
        redRecordMapper.insertSelective(entity);

        list.parallelStream().forEach(amount -> {
            RedDetail detail1 = new RedDetail();
            detail1.setAmount(BigDecimal.valueOf(amount));//列表中的每个金额
            detail1.setRecordId(entity.getId());
            detailMapper.insertSelective(detail1);
        });
    }

    /**
     * 抢红包请求逻辑 (由点击和拆开红包2个逻辑组成)
     *
     * @param userId       用户账号
     * @param redPacketKey 红包的唯一标识
     * @throws Exception
     */
    public Integer robRequest(final Integer userId, final String redPacketKey) throws Exception {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //TODO:判断当前用户是否抢过红包
        final String robRedPacketUserKey = redPacketKey + userId + ":rob";
        Object valueInCache = valueOperations.get(robRedPacketUserKey);
        //TODO:如果缓存中存在这个用户抢到红包的key，那么直接返回抢到的金额大小
        if (valueInCache != null) {
            return Integer.valueOf(String.valueOf(valueInCache));
        }
        //TODO:点击红包逻辑
        Boolean ifExist = clickRequest(redPacketKey);
        if (ifExist) {
            //TODO:基于redis实现分布式锁，对高并发情况下的抢红包异常问题进行解决(保证每个人只能抢一个红包，确保人与红包为1:1)
            final String lockKey = redPacketKey + userId + "-lock";
            Boolean lock = valueOperations.setIfAbsent(lockKey, redPacketKey);
            try {
                if (lock) {
                    redisTemplate.expire(lockKey,48L,TimeUnit.HOURS);//给分布式锁设置过期时间

                    //TODO:拆开红包逻辑
                    //TODO:从缓存中弹出一个随机红包金额
                    Object value = redisTemplate.opsForList().rightPop(redPacketKey);
                    if (value != null) {
                        //TODO:红包个数减一
                        final String totalKey = redPacketKey + ":total";

                        valueOperations.increment(totalKey, -1L);

                        //TODO:入库
                        final Integer amount = Integer.valueOf(String.valueOf(value));
                        recordRedPacketRobbed(userId, redPacketKey, amount);
                        log.info("--增加分布式锁--当前用户抢到的红包:userId={},redPacketKey={},amount={}", userId, redPacketKey, amount);

                        //TODO:将当前用户抢到的红包金额塞入缓存中
                        valueOperations.set(robRedPacketUserKey, value, 24L, TimeUnit.HOURS);//设置24小时后key过期
                        return amount;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //TODO:注意：这里是不需要释放锁的。因为红包一发出去，就产生了一个新的key,当红包金额全部抢完，key的生命周期就结束。
            }
        }
        return null;
    }

    //TODO:判断缓存中系统中的红包个数
    private Boolean clickRequest(final String redPacketKey) {
        //TODO:通过红包个数key得到缓存中的红包数量
        Object totalNums = redisTemplate.opsForValue().get(redPacketKey + ":total");
        if (totalNums != null && Integer.valueOf(String.valueOf(totalNums)) > 0) {
            return true;
        }
        return false;
    }

    //TODO:记录抢到的红包明细
    private void recordRedPacketRobbed(final Integer userId, final String redPacketKey, final Integer amount) throws Exception {
        RedRobRecord record = new RedRobRecord();
        record.setUserId(userId);
        record.setAmount(BigDecimal.valueOf(amount));
        record.setRedPacket(redPacketKey);
        record.setRobTime(DateTime.now().toJdkDate());

        recordMapper.insertSelective(record);
    }


}

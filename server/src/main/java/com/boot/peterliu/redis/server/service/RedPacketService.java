package com.boot.peterliu.redis.server.service;

import cn.hutool.core.lang.Snowflake;
import com.boot.peterliu.redis.model.dto.RedPacketDto;
import com.boot.peterliu.redis.model.entity.RedDetail;
import com.boot.peterliu.redis.model.entity.RedRecord;
import com.boot.peterliu.redis.model.mapper.RedDetailMapper;
import com.boot.peterliu.redis.model.mapper.RedRecordMapper;
import com.boot.peterliu.redis.server.utils.RedPacketUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
     *
     * @param packetDto
     * @param list      每个红包金额组成的list
     * @param packetKey
     */
    private void recordRedPacket(RedPacketDto packetDto, List<Integer> list, final String packetKey) {
        RedRecord entity = new RedRecord();
        entity.setAmount(BigDecimal.valueOf(packetDto.getAmount()));
        entity.setCreateTime(new Date());
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


}

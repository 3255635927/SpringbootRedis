package com.boot.peterliu.redis.test;

import com.boot.peterliu.redis.server.utils.RedPacketUtil;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/28 20:46
 * @Description:二倍均值法测试，金额以分为单位
 */
public class RedPacketTest {
    public static void main(String[] args) {
        Integer totalAmount=100;
        Integer totalPeople=10;

        Integer total=0;

        List<Integer> list = RedPacketUtil.divideAmount(totalAmount, totalPeople);
        System.out.println("均分后的金额:"+list);

        for(Integer e:list){
            total+=e;
        }
        System.out.println("每个金额之和为:"+total);
    }
}

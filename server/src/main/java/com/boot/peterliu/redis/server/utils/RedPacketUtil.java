package com.boot.peterliu.redis.server.utils;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/23 15:53
 * @Description:使用二倍均值算法生成红包金额 条件：(1)每个小红包金额至少是1分钱（2）红包总金额=每个小红包的金额之和
 */
public class RedPacketUtil {

    /**
     * 均分金额
     *
     * @param totalAmount 剩余总金额(以分为单位)
     * @param totalPeople 剩余总人数
     * @return
     */
    public static List<Integer> divideAmount(final Integer totalAmount, final Integer totalPeople) {
        List<Integer> list = Lists.newLinkedList();
        if (totalAmount > 0 && totalPeople > 0) {
            Integer restAmount = totalAmount;//剩余金额
            Integer restPeople = totalPeople;//剩余人数

            Random random = new Random();
            int amount;
            //每个人拿到的红包金额至少是1分及以上
            for (int i = 0; i < totalPeople - 1; i++) {
                //左闭右开区间: [1,剩余金额/剩余人数 的除数的2倍 )
                amount = random.nextInt(restAmount / restPeople * 2 - 1) + 1;//每个人取到的金额数量
                restAmount-=amount;//每个人抢到红包后，将在剩余红包金额中减去被抢的红包金额数量
                restPeople--;//减去以抢红包的人数

                list.add(amount);//把每次抢到的金额存放到有序链表中
            }
            list.add(restAmount);//把最后一个人抢到的剩余的金额存放到有序链表
        }
        return list;
    }
}

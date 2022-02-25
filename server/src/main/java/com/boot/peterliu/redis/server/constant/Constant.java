package com.boot.peterliu.redis.server.constant;

/**
 * 系统统一常量配置
 */
public class Constant {
    //String数据类型的key前缀常量
    public static final String RedisStringPrefix="SpringBootRedis:String:";

    //List数据类型的key前缀常量(商户)
    public static final String RedisListPrefix="SpringBootRedis:List:User:";

    //List数据类型-通告key
    public static final String RedisListNoticeKey="SpringBootRedis:List:Queue:Notice";

}

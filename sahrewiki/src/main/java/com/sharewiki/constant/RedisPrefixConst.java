package com.sharewiki.constant;

/**
 * redis常量
 *
 */
public class RedisPrefixConst {

    /**
     * 验证码过期时间
     */
    public static final long CODE_EXPIRE_TIME = 15 * 60;

    /**
     * 验证码
     */
    public static final String USER_CODE_KEY = "code:";
}

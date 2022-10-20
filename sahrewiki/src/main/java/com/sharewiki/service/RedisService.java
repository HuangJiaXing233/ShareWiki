package com.sharewiki.service;

/**
 * redis操作
 *
 * @author IKY
 * @date 2022/10/20
 * @since 1.0.0
 **/
public interface RedisService {

    /**
     * 保存属性（有过期时间）
     *
     * @param key   key值
     * @param value value值
     * @param time  时间戳
     */
    void set(String key,Object value,long time);

    /**
     * 保存属性
     *
     * @param key   key值
     * @param value value值
     */
    void set(String key, Object value);

    /**
     * 获取属性
     *
     * @param key key值
     * @return 返回对象
     */
    Object get(String key);

    /**
     * 删除属性
     *
     * @param key key值
     * @return 返回成功
     */
    Boolean del(String key);

}

package com.ahzak.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/14 16:25
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
@ConditionalOnClass({StringRedisTemplate.class})
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static StringRedisTemplate STRINGREDISTEMPLATE;

    @PostConstruct
    private void init() {
        STRINGREDISTEMPLATE = stringRedisTemplate;
    }

    /**
     * 通过lua脚本保证释放锁的操作具有原子性
     */
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final DefaultRedisScript<Long> releaseLockRedisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT, Long.class);
    private static final long RELEASE_LOCK_SUCCESS_RESULT = 1L;


    /**
     * 尝试获取分布式锁
     * 目前的分布式锁基于redis实现
     * https://blog.csdn.net/yb223731/article/details/90349502
     *
     * @param key        锁ID
     * @param clientId   客户端ID
     * @param expireTime 锁的自动过期时间
     * @param timeUnit   过期时间的单位
     * @return boolean 获取锁成功返回true, 否则返回false
     * @author Zhu Kaixiao
     * @date 2019/11/14 9:24
     */
    public static boolean tryLock(String key, String clientId, long expireTime, TimeUnit timeUnit) {
        Boolean result = STRINGREDISTEMPLATE.opsForValue().setIfAbsent(key, clientId, expireTime, timeUnit);
        return result;
    }

    public static boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
        return tryLock(key, SystemContextUtils.getClientId(), expireTime, timeUnit);
    }

    public static boolean tryLock(String key, long expireTime) {
        return tryLock(key, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 释放锁
     *
     * @param key      锁ID
     * @param clientId 客户端ID
     * @return boolean 是否成功
     * @author Zhu Kaixiao
     * @date 2019/11/14 9:26
     */
    public static boolean releaseLock(String key, String clientId) {
        Long result = STRINGREDISTEMPLATE.execute(releaseLockRedisScript, Collections.singletonList(key), clientId);
        return Objects.equals(result, RELEASE_LOCK_SUCCESS_RESULT);
    }


    /**
     * 释放锁
     *
     * @param key 锁ID
     * @return boolean 是否成功
     * @author Zhu Kaixiao
     * @date 2019/11/14 9:26
     */
    public static boolean releaseLock(String key) {
        return releaseLock(key, SystemContextUtils.getClientId());
    }


    // -------------------------------------------------------------------------------------------------------------

    private static final String RECORD_TIME_KEY_PREFIX = "redis_key_store_time:";

    public static void setAndRecordTime(String key, String value) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value);
        STRINGREDISTEMPLATE.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()));
    }

    public static void setAndRecordTime(String key, String value, Duration duration) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value, duration);
        STRINGREDISTEMPLATE.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()), duration);
    }

    public static void setAndRecordTime(String key, String value, long timeout, TimeUnit timeUnit) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value, timeout, timeUnit);
        STRINGREDISTEMPLATE.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()), timeout, timeUnit);
    }

    /**
     * 已毫秒为单位获取key已存在的时间
     * 使用本方法的key必须是先用setAndRecordTime方法保存的值, 不然获取的都是-1
     *
     * @param key key
     * @return long
     * @author Zhu Kaixiao
     * @date 2020/6/29 14:13
     */
    public static long getAge(String key) {
        return getAge(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取key已存在的时间
     * 使用本方法的key必须是先用setAndRecordTime方法保存的值, 不然获取的都是-1
     *
     * @param key      key
     * @param timeUnit 时间单位
     * @return long
     * @author Zhu Kaixiao
     * @date 2020/6/29 14:11
     */
    public static long getAge(String key, TimeUnit timeUnit) {
        String storeTime = STRINGREDISTEMPLATE.opsForValue().get(RECORD_TIME_KEY_PREFIX + key);
        if (storeTime == null) {
            return -1;
        }
        long st = Long.parseLong(storeTime);
        long t = System.currentTimeMillis() - st;
        long r = timeUnit.convert(t, TimeUnit.MILLISECONDS);
        return r;
    }


    public static void set(String key, String value) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value);
    }

    public static void set(String key, String value, Duration duration) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value, duration);
    }

    public static void set(String key, String value, long timeout, TimeUnit timeUnit) {
        STRINGREDISTEMPLATE.opsForValue().set(key, value, timeout, timeUnit);
    }

    public static String get(String key) {
        return STRINGREDISTEMPLATE.opsForValue().get(key);
    }

    public static boolean exist(String key) {
        return STRINGREDISTEMPLATE.opsForValue().get(key) != null;
    }


}

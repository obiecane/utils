//package com.ahzak.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.Collections;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author Zhu Kaixiao
// * @version 1.0
// * @date 2019/11/14 16:25
// * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
// * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
// */
//@Slf4j
//@Component
//@ConditionalOnClass({StringRedisTemplate.class})
//public class RedisUtil {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    private static StringRedisTemplate STRINGREDISTEMPLATE;
//
//    @PostConstruct
//    private void init() {
//        STRINGREDISTEMPLATE = stringRedisTemplate;
//    }
//
//    /**
//     * 通过lua脚本保证释放锁的操作具有原子性
//     */
//    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//    private static final DefaultRedisScript<Long> releaseLockRedisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT, Long.class);
//    private static final long RELEASE_LOCK_SUCCESS_RESULT = 1L;
//
//
//    /**
//     * todo 锁范围细到商品
//     * 尝试获取分布式锁
//     * 目前的分布式锁基于redis实现
//     * https://blog.csdn.net/yb223731/article/details/90349502
//     *
//     * @param key        锁ID
//     * @param clientId   客户端ID
//     * @param expireTime 锁的自动过期时间
//     * @param timeUnit   过期时间的单位
//     * @return boolean 获取锁成功返回true, 否则返回false
//     * @author Zhu Kaixiao
//     * @date 2019/11/14 9:24
//     */
//    public static boolean tryLock(String key, String clientId, long expireTime, TimeUnit timeUnit) {
//        Boolean result = STRINGREDISTEMPLATE.opsForValue().setIfAbsent(key, clientId, expireTime, timeUnit);
//        return result;
//    }
//
//    public static boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
//        return tryLock(key, SystemContextUtils.getClientId(), expireTime, timeUnit);
//    }
//
//    public static boolean tryLock(String key, long expireTime) {
//        return tryLock(key, expireTime, TimeUnit.SECONDS);
//    }
//
//
//    /**
//     * 释放锁
//     *
//     * @param key      锁ID
//     * @param clientId 客户端ID
//     * @return boolean 是否成功
//     * @author Zhu Kaixiao
//     * @date 2019/11/14 9:26
//     */
//    public static boolean releaseLock(String key, String clientId) {
//        Long result = STRINGREDISTEMPLATE.execute(releaseLockRedisScript, Collections.singletonList(key), clientId);
//        return Objects.equals(result, RELEASE_LOCK_SUCCESS_RESULT);
//    }
//
//
//    /**
//     * 释放锁
//     *
//     * @param key 锁ID
//     * @return boolean 是否成功
//     * @author Zhu Kaixiao
//     * @date 2019/11/14 9:26
//     */
//    public static boolean releaseLock(String key) {
//        return releaseLock(key, SystemContextUtils.getClientId());
//    }
//
//
//
//}

package com.ahzak.utils;

import cn.hutool.core.util.RandomUtil;
import com.ahzak.utils.exception.GlobalException;
import com.ahzak.utils.notify.NotifyUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/29 14:27
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class CaptchaUtil {

    /**
     * 将验证码和redis中的值进行比对, 如果两值相等, 则返回true
     *
     * @param key  redis中的key
     * @param code 需要比对的验证码
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/6/30 9:18
     */
    public static boolean match(String key, String code) {
        String captcha = getCaptcha(key);
        if (StringUtils.isBlank(captcha)) {
            return false;
        }
        if (!captcha.equals(code)) {
            return false;
        }

        return true;
    }

    /**
     * 校验验证码, 如果验证码和redis中的不一致或redis中没有指定的key
     * 将会触发异常
     *
     * @param key
     * @param code
     * @return void
     * @author Zhu Kaixiao
     * @date 2020/6/30 9:20
     */
    public static void valid(String key, String code) {
        String captcha = getCaptcha(key);
        if (StringUtils.isBlank(captcha)) {
            throw new GlobalException("验证码已过期");
        }
        if (!captcha.equals(code)) {
            throw new GlobalException("验证码错误");
        }
    }

    public static String getCaptcha(String key) {
        return RedisUtil.get(key);
    }

    public static void sendSmsCaptcha(String phone, String key, String templateSymbol) {
        sendSmsCaptcha(phone, key, templateSymbol, () -> {
        });
    }


    /**
     * 发送验证码
     *
     * @param phone          收信手机号
     * @param key            redis的key
     * @param templateSymbol 模板标识
     * @author Zhu Kaixiao
     * @date 2020/6/22 10:25
     */
    public static void sendSmsCaptcha(String phone, String key, String templateSymbol, Runnable validRunnable) {
        doSendCaptcha(phone, key, templateSymbol, validRunnable, NotifyUtil::sendSmsNotify);
    }


    public static void sendEmailCaptcha(String email, String key, String templateSymbol) {
        sendEmailCaptcha(email, key, templateSymbol, () -> {
        });
    }

    public static void sendEmailCaptcha(String email, String key, String templateSymbol, Runnable validRunnable) {
        doSendCaptcha(email, key, templateSymbol, validRunnable, NotifyUtil::sendEmailNotify);
    }


    public static void sendCaptcha(SendType type, String target, String key, String templateSymbol) {
        sendCaptcha(type, target, key, templateSymbol, () -> {
        });
    }

    public static void sendCaptcha(SendType type, String target, String key, String templateSymbol, Runnable validRunnable) {
        if (type == SendType.SMS) {
            sendSmsCaptcha(target, key, templateSymbol, validRunnable);
        } else if (type == SendType.EMAIL) {
            sendEmailCaptcha(target, key, templateSymbol, validRunnable);
        }
    }


    private static void doSendCaptcha(String target, String key, String templateSymbol, Runnable validRunnable, Sender sender) {
        Assert.notBlank(target, "验证码接收地址不能为空");

        // 1. 校验
        //    验证码上一次发送时间不超过1分钟不发送, 防止重复发送
        long keyAge = RedisUtil.getAge(key, TimeUnit.MINUTES);
        if (keyAge > -1 && keyAge < 1) {
            throw new GlobalException("请勿重复发送!");
        }

        // 校验
        validRunnable.run();

        // 2. 生成验证码
        String captcha = RandomUtil.randomString(RandomUtil.BASE_NUMBER, 6);

        // 3. 准备消息模板变量值
        Map<String, String> map = Collections.singletonMap("code", captcha);

        // 4. 发送验证码
        sender.send(templateSymbol, map, target);

        // 5. 保存到redis, 防止重复发送;
        RedisUtil.setAndRecordTime(key, captcha, Duration.ofMinutes(5));
    }


    @FunctionalInterface
    private interface Sender {
        void send(String templateSymbol, Map<String, String> map, String target);
    }

    public enum SendType {
        SMS, EMAIL
    }

}

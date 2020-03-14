package com.ahzak.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/12/26 9:49
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class DesensitizationUtil {

    private static final int PHONE_LENGTH = 11;

    private static final Pattern TEL_PATTERN = Pattern.compile("\\(?\\d{2,3}[)\\-_](\\d{7,8})");

    /**
     * 对手机号进行脱敏
     * 13707096483  -> 137****6483
     *
     * @param phone
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/12/26 9:50
     */
    public static String dePhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return phone;
        }
        if (phone.length() != PHONE_LENGTH) {
            return phone;
        }
        final char[] chars = phone.toCharArray();
        chars[3] = chars[4] = chars[5] = chars[6] = '*';
        return String.valueOf(chars);
    }


    public static void main(String[] args) {
        final String s = dePhone("13937033596");
        System.out.println(s);

        final String s1 = deTel("(020)3764748");
        System.out.println(s1);
    }

    /**
     * 对座机号进行脱敏
     * (010)32764748 -> (010)32***748
     * 020-88888888  -> 020-88***888
     *
     * @param tel
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/12/26 10:01
     */
    public static String deTel(String tel) {
        if (StringUtils.isBlank(tel)) {
            return tel;
        }

        final Matcher matcher = TEL_PATTERN.matcher(tel);
        if (matcher.find()) {
            final String num = matcher.group(1);
            char[] chars = num.toCharArray();
            if (chars.length == 7) {
                char[] newChars = new char[8];
                System.arraycopy(chars, 0, newChars, 0, 2);
                System.arraycopy(chars, 4, newChars, 5, 3);
                chars = newChars;
            }
            chars[2] = chars[3] = chars[4] = '*';
            return matcher.group().replaceFirst(num, String.valueOf(chars));
        }
        return tel;
    }


}

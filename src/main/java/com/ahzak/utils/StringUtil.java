package com.ahzak.utils;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/24 15:12
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class StringUtil {

    public static boolean isEnglish(String str) {
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (aChar > 255) {
                return false;
            }
        }
        return true;
    }


    public static int words(String str) {
        return str.trim().split(" ").length;
    }
}

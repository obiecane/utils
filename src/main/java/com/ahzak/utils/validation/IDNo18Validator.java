package com.ahzak.utils.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/30 10:38
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class IDNo18Validator implements ConstraintValidator<IDNo18, String> {

    /**
     * 校验身份证号码
     *
     * <p>
     * 适用于18位的二代身份证号码
     * </p>
     *
     * @param value 身份证号码
     * @return true - 校验通过<br>
     * false - 校验不通过
     * @throws IllegalArgumentException 如果身份证号码为空或长度不为18位或不满足身份证号码组成规则
     *                                  <i>6位地址码+
     *                                  出生年月日YYYYMMDD+3位顺序码
     *                                  +0~9或X(x)校验码</i>
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        // 校验身份证号码的长度
        if (!checkStrLength(value, 18)) {
            return false;
        }
        // 匹配身份证号码的正则表达式
        if (!regexMatch(value, REGEX_ID_NO_18)) {
            return false;
        }
        // 校验身份证号码的验证码
        return validateCheckNumber(value);
    }

    /**
     * 18位二代身份证号码的正则表达式
     */
    public static final String REGEX_ID_NO_18 = "^"
            + "\\d{6}" // 6位地区码
            + "(18|19|([23]\\d))\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}" // 3位顺序码
            + "[0-9Xx]" // 校验码
            + "$";


    /**
     * 15位一代身份证号码的正则表达式
     */
    public static final String REGEX_ID_NO_15 = "^"
            + "\\d{6}" // 6位地区码
            + "\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}"// 3位顺序码
            + "$";


    /**
     * 校验字符串长度
     *
     * @param inputString 字符串
     * @param len         预期长度
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean checkStrLength(String inputString, int len) {
        return inputString != null && inputString.length() == len;
    }

    /**
     * 匹配正则表达式
     *
     * @param inputString 字符串
     * @param regex       正则表达式
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean regexMatch(String inputString, String regex) {
        return inputString.matches(regex);
    }

    /**
     * 校验码校验
     * <p>
     * 适用于18位的二代身份证号码
     * </p>
     *
     * @param IDNo18 身份证号码
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean validateCheckNumber(String IDNo18) {
        // 加权因子
        int[] W = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] IDNoArray = IDNo18.toCharArray();
        int sum = 0;
        for (int i = 0; i < W.length; i++) {
            sum += Integer.parseInt(String.valueOf(IDNoArray[i])) * W[i];
        }
        // 校验位是X，则表示10
        if (IDNoArray[17] == 'X' || IDNoArray[17] == 'x') {
            sum += 10;
        } else {
            sum += Integer.parseInt(String.valueOf(IDNoArray[17]));
        }
        // 如果除11模1，则校验通过
        return sum % 11 == 1;
    }

}

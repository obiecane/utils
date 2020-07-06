package com.ahzak.utils;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本模板
 * 用于将模板中的变量占位符替换成变量值
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/4/7 10:12
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class TextTemplate {

    /**
     * 变量匹配表达式
     * 格式: 以{{开头 以}}结尾, 中间变量名为大小写字母,数字,下划线组成, 且第一位只能是大小写字母
     */
    private static final Pattern template_var_pattern = Pattern.compile("\\{\\{([A-Za-z]+?[A-Za-z0-9_]*?)}}");

    private static final Map<String, TextTemplate> TEXT_TEMPLATE_CACHE = new ConcurrentHashMap<>();

    /**
     * 模板
     *
     * @author Zhu Kaixiao
     * @date 2020/4/7 10:14
     */
    private String templateStr;

    /**
     * 模板变量
     *
     * @author Zhu Kaixiao
     * @date 2020/4/7 10:14
     */
    private LinkedHashMap<Region, String> templateVer;


    private TextTemplate(String templateStr) {
        this.templateStr = Objects.requireNonNull(templateStr);
        templateVer = new LinkedHashMap<>();
        final Matcher matcher = template_var_pattern.matcher(templateStr);
        while (matcher.find()) {
            templateVer.put(new Region(matcher.start(), matcher.end()), matcher.group(1));
        }
    }


    /**
     * 以严格模式将变量值填充到模板中
     * 在严格模式中, 传入的变量值必须匹配模板中所有的变量
     * 否则将抛出异常
     *
     * @param vals 变量值
     * @return java.lang.String 变量填充后的文本
     * @throws IllegalArgumentException 当传入的变量值不能匹配模板中所有的变量时抛出此异常
     * @author Zhu Kaixiao
     * @date 2020/4/9 14:36
     */
    public String mold(Map<String, String> vals) {
        return mold(vals, true);
    }

    /**
     * 以指定填充模式将变量值填充到模板中
     * 严格模式: 传入的变量值必须匹配模板中所有的变量, 否则将抛出异常
     * 松散模式: 只将能匹配的变量替换成变量值, 其他未能匹配上的变量不做任何改动
     *
     * @param vals  变量值
     * @param rigor true 严格模式, false 松散模式
     * @return java.lang.String
     * @throws IllegalArgumentException 在严格模式下, 当传入的变量值不能匹配模板中所有的变量时抛出此异常
     * @author Zhu Kaixiao
     * @date 2020/4/9 14:37
     */
    public String mold(Map<String, String> vals, boolean rigor) {
        vals = vals == null ? Collections.emptyMap() : vals;

        StringBuilder sb = new StringBuilder(templateStr);
        int offset = 0;
        for (Map.Entry<Region, String> entry : templateVer.entrySet()) {
            final Region region = entry.getKey();
            String v = vals.get(entry.getValue());
            if (v == null) {
                continue;
            }
            sb.replace(region.start + offset, region.end + offset, v);
            offset += v.length() - (region.end - region.start);
        }

        if (rigor) {
            Set<String> tvarSet = new HashSet<>(this.templateVer.values());
            tvarSet.removeAll(vals.keySet());
            if (!tvarSet.isEmpty()) {
                throw new IllegalArgumentException("模板填充错误: 缺少变量[" + StringUtils.join(tvarSet, ",") + "]");
            }
        }

        return sb.toString();
    }


    /**
     * 解析一个信息模板
     *
     * @param templateStr
     * @return com.jeecms.market.utils.TextTemplate
     * @author Zhu Kaixiao
     * @date 2020/6/19 9:39
     */
    public static TextTemplate compile(String templateStr) {
        TextTemplate textTemplate = TEXT_TEMPLATE_CACHE.get(templateStr);
        if (textTemplate == null) {
            textTemplate = new TextTemplate(templateStr);
            TEXT_TEMPLATE_CACHE.put(templateStr, textTemplate);
        }
        return textTemplate;
    }


    /**
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/19 9:39
     * @see TextTemplate#compile(String)
     * @see TextTemplate#mold(Map)
     */
    public static String mold(String templateStr, Map<String, String> vals) {
        return mold(templateStr, vals, true);
    }

    /**
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/19 9:40
     * @see TextTemplate#compile(String)
     * @see TextTemplate#mold(Map, boolean)
     */
    public static String mold(String templateStr, Map<String, String> vals, boolean rigor) {
        return TextTemplate.compile(templateStr).mold(vals, rigor);
    }




    public static void main(String[] args) {
        String t = "模板测试{13},{13aaa}, {{code}}, {a{b}c}  {ab}{cc}  {order_code}, {orderCode}, {orderCode12}, {orderCode12&*} \n哈塞京 {code},";
        TextTemplate st = TextTemplate.compile(t);
        Map<String, String> val = new HashMap<>();
        val.put("code", "1");
        val.put("order_code", "ijvoiakfvio2389aksjd2j");
        val.put("orderCode", "jkaskdjflww");
        val.put("orderCode12", "hahahha");
        System.out.println(st.mold(val, false));
    }


    @AllArgsConstructor
    private static class Region {
        int start;
        int end;


        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof Region && ((Region) obj).start == this.start && ((Region) obj).end == this.end);
        }
    }
}

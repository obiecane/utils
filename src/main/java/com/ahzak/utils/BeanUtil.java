package com.ahzak.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/3/13 17:43
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {

    private BeanUtil() {
    }

    public static <E, V> List<V> getFieldValue(Collection<E> objs, Function<E, V> function) {
        final List<V> values = objs.stream().map(function).collect(Collectors.toList());
        return values;
    }

    public static <E, V> V getFieldValue(E obj, Function<E, V> function) {
        final V value = function.apply(obj);
        return value;
    }


    public static void main(String[] args) {

    }

}

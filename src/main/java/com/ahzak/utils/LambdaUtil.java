package com.ahzak.utils;

public class LambdaUtil {


    /** 编写一个泛型方法对异常进行包装 */
    public static <E extends Exception> void doThrow(Exception e) throws E {
        throw (E)e;
    }
}

package com.ahzak.utils;

public class LambdaUtil {


    /** 编写一个泛型方法对异常进行包装 */
    public static <R, E extends Exception> R doThrow(Exception e) throws E {
        throw (E)e;
    }
}

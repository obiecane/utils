package com.ahzak.utils;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;


/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/6/11 10:37
 */
public class HibernateUtils {

    /**
     * 如果传入的对象是HibernateProxy类型, 则透过代理, 取出真实对象
     * @param obj 对象
     * @return java.lang.Object
     * @author Zhu Kaixiao
     * @date 2019/6/11 10:43
     **/
    public static Object throughProxy(Object obj) {
        if (obj instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) obj;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            obj = li.getImplementation();
        }
        return obj;
    }



    private HibernateUtils() {
    }

}

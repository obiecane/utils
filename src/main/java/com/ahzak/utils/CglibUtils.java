package com.ahzak.utils;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;


@Slf4j
public class CglibUtils {


    /**
     * 动态添加类的属性
     * 通过cglib创建目标类的子类, 并增加指定的字段
     * 然后会把原对象的属性值和待增加的属性的属性值复制到新的对象中
     * @param dest 待增强的类对象
     * @param addProperties 待增加的属性及属性值
     * @return 增强后的对象
     * @author Zhu Kaixiao
     * @date 2019
     */
    public static Object dynamicField(Object dest, Map<String, ?> addProperties) {
        // 动态创建对象, 增加指定属性
        Map<String, Class> propertyMap = Maps.newHashMapWithExpectedSize(addProperties.size());
        for (String key : addProperties.keySet()) {
            propertyMap.put(key, addProperties.get(key).getClass());
        }
        DynamicBean dynamicBean = new DynamicBean(dest.getClass(), propertyMap);

        // 把扩展属性值复制到新对象
        addProperties.forEach((k, v) -> dynamicBean.setValue(k, v));
        Object target = dynamicBean.getTarget();
        // 把原对象中的属性值复制到新对象
        BeanUtil.copyProperties(dest, target);

        return target;
    }

    private static class DynamicBean {
        /**
         * 目标对象
         */
        private Object target;

        /**
         * 属性集合
         */
        private BeanMap beanMap;

        DynamicBean(Class superclass, Map<String, Class> propertyMap) {
            this.target = generateBean(superclass, propertyMap);
            this.beanMap = BeanMap.create(this.target);
        }


        /**
         * bean 添加属性和值
         *
         * @param property
         * @param value
         */
        void setValue(String property, Object value) {
            log.debug("beanMap.put => {}:{}", property, value);
            beanMap.put(property, value);
        }

        /**
         * 获取属性值
         *
         * @param property
         * @return
         */
        Object getValue(String property) {
            return beanMap.get(property);
        }

        /**
         * 获取对象
         *
         * @return
         */
        Object getTarget() {
            return this.target;
        }


        /**
         * 根据属性生成对象
         *
         * @param superclass
         * @param propertyMap
         * @return
         */
        private Object generateBean(Class superclass, Map<String, Class> propertyMap) {
            BeanGenerator generator =new BeanGenerator();
            if(null != superclass) {
                generator.setSuperclass(superclass);
            }
            BeanGenerator.addProperties(generator, propertyMap);
            return generator.create();
        }
    }
}

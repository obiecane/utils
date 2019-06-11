package com.ahzak.utils.bean;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * BeanUtils copyProperties 过滤null
 * 
 * @author: tom
 * @date: 2018年4月11日 下午3:25:30
 */
public class MyBeanUtils extends org.springframework.beans.BeanUtils {

	/**
	 * 拷贝一个对象至另一个对象，（，为空属性不拷贝，子对象无法拷贝）
	 * 
	 * @Title: copyProperties
	 * @Description:
	 * @param: @param
	 *             source
	 * @param: @param
	 *             target
	 * @param: @throws
	 *             BeansException
	 * @return: void
	 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), 
						targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						// 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
						if (value != null) {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(
									writeMethod.getDeclaringClass()
									.getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
					} catch (Throwable ex) {
						throw new FatalBeanException(""
								+ "Could not copy properties from source to target",
								ex);
					}
				}
			}
		}
	}

	/**
	 * 转换为Double类型
	 */
	public static Double convertToDouble(final Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return NumberUtils.toDouble(StringUtils.trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float convertToFloat(final Object val) {
		return convertToDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long convertToLong(final Object val) {
		return convertToDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer convertToInteger(final Object val) {
		return convertToLong(val).intValue();
	}

	/**
	 * 转换为Boolean类型 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will
	 * return true. Otherwise, false is returned.
	 */
	public static Boolean convertToBoolean(final Object val) {
		if (val == null) {
			return false;
		}
		return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
	}

	/**
	 * 转换为字符串
	 * 
	 * @param obj
	 *            对象
	 * @return
	 */
	public static String convertToString(final Object obj) {
		return convertToString(obj, StringUtils.EMPTY);
	}

	/**
	 * 如果对象为空，则使用defaultVal值
	 * 
	 * @param obj
	 *            Object
	 * @param defaultVal
	 *            默认值
	 * @return
	 */
	public static String convertToString(final Object obj, final String defaultVal) {
		return obj == null ? defaultVal : obj.toString();
	}

	/**
	 * 空转空字符串（"" to "" ; null to "" ; "null" to "" ; "NULL" to "" ; "Null" to
	 * ""）
	 * 
	 * @param val
	 *            需转换的值
	 * @return 返回转换后的值
	 */
	public static String convertToStringIgnoreNull(final Object val) {
		return MyBeanUtils.convertToStringIgnoreNull(val, StringUtils.EMPTY);
	}

	/**
	 * 空对象转空字符串 （"" to defaultVal ; null to defaultVal ; "null" to defaultVal ;
	 * "NULL" to defaultVal ; "Null" to defaultVal）
	 * 
	 * @param val
	 *            需转换的值
	 * @param defaultVal
	 *            默认值
	 * @return 返回转换后的值
	 */
	public static String convertToStringIgnoreNull(final Object val, String defaultVal) {
		String str = MyBeanUtils.convertToString(val);
		return !"".equals(str) && !"null".equalsIgnoreCase(str.trim()) ? str : defaultVal;
	}

	/**
	 * 从指定的对象中返回指定属性的值, 不论该属性是在本类中还是在父类中,
	 * 只有有相应的的getter方法便能获取该属性的值
	 *
	 * @param obj 指定的对象
	 * @param fieldName 属性的名称
	 * @return java.lang.Object
	 * @author Zhu Kaixiao
	 * @date 2019/6/4 9:49
	 * @exception NoSuchMethodException 如果传入的属性没有getter方法
	 **/
	public static Object getProperty(Object obj, String fieldName)
			throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Class<?> aClass = obj.getClass();
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(aClass, fieldName);
		if (propertyDescriptor == null) {
			throw new NoSuchMethodException("getter方法不存在: " + aClass.getName() + ":" + fieldName);
		}

		Method readMethod = propertyDescriptor.getReadMethod();
		Object ret = readMethod.invoke(obj);
		return ret;
	}

}

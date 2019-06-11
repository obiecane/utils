package com.ahzak.utils.excel.aspect;

import com.ahzak.utils.HibernateUtils;
import com.ahzak.utils.bean.MyBeanUtils;
import com.ahzak.utils.excel.ExcelUtils;
import com.ahzak.utils.excel.annotation.ExcelExport;
import com.ahzak.utils.excel.annotation.ExcelSerialize;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/6/3 16:03
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ExcelAspect {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    /**
     * 定义切入点
     */
    @Pointcut(value = "@annotation(excelExport)")
    public void serviceStatistics(ExcelExport excelExport) {
    }

    @AfterReturning(value = "serviceStatistics(excelExport)", returning = "returnValue",
            argNames = "joinPoint,excelExport,returnValue")
    public Object postAdvice(JoinPoint joinPoint, ExcelExport excelExport, Object returnValue) {
        // 只有返回list才开始生成Excel
        if (!(returnValue instanceof List)) {
            return returnValue;
        }
        List<Object> objectList = (List<Object>) returnValue;

        // 1. 根据注解解析List,
        Map<String, Object> serialFieldMap = convertToTree(excelExport);
        List<String> headList = fetchHeadList(excelExport);
        List<List<Object>> valuesList;
        try {
            valuesList = fetchValuesList(objectList, serialFieldMap);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("excel导出失败: {}#{}\t[{}]",
                    joinPoint.getTarget().getClass().getName(),
                    joinPoint.getSignature().getName(),
                    objectList, e);
            throw new RuntimeException(e);
        }

        // 3. 写出Excel到输出流
        String excelName = excelExport.excelName() + ".xls";
        response.setContentType("text/html;charset=utf-8");
        response.setContentType("application/x-msdownload;");
        response.setHeader("Content-disposition", "attachment; filename="
                + new String(excelName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

        try (
                OutputStream out = response.getOutputStream()
        ) {
            ExcelUtils.genAndWriteExcel(headList, valuesList, excelExport.excelName(), out);
        } catch (IOException e) {
            log.error("excel导出失败: {}#{}",
                    joinPoint.getTarget().getClass().getName(),
                    joinPoint.getSignature().getName(), e);
        }

        return returnValue;
    }


    /**
     * 从注解中提取excel表头信息
     * @param excelExport 注解对象
     * @return java.util.List<java.lang.String>
     * @author Zhu Kaixiao
     * @date 2019/6/5 12:03
     **/
    private List<String> fetchHeadList(ExcelExport excelExport) {
        List<String> headList = new LinkedList<>();
        ExcelSerialize[] values = excelExport.value();

        for (ExcelSerialize excelSerialize : values) {
            String[] heads = excelSerialize.heads();
            headList.addAll(Arrays.asList(heads));
        }

        return headList;
    }

    /**
     * 提取填充excel的值
     * @param objList 对象列表
     * @param serialFieldMap 字段信息
     * @return java.util.List<java.util.List<java.lang.Object>>
     * @author Zhu Kaixiao
     * @date 2019/6/5 12:04
     **/
    private List<List<Object>> fetchValuesList(List<Object> objList, Map<String, Object> serialFieldMap)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<List<Object>> ret = new LinkedList<>();

        for (Object obj : objList) {
            List<Object> list = resolve(obj, serialFieldMap);
            ret.add(list);
        }

        return ret;
    }



    /**
     * 将注解中配置的excel导出信息转换成树状结构
     * 如:
     * @ExcelExport(
     *         excelName = "excel file name",
     *         value = {
     *                 @ExcelSerialize(clazz = AuthInfo.class, fields = {"id", "authCode",  "customer"}, heads = {"id", "授权码"}),
     *                 @ExcelSerialize(clazz = Customer.class, fields = {"id", "address", "company"}, heads = {"customer id", "地址"}),
     *                 @ExcelSerialize(clazz = CustomerCompany.class, fields = {"id", "companyName"}, heads = {"company id", "公司名"}),
     *         }
     *     )
     *
     *  会被转换成
     *
     * {
     *     class: AuthInfo
     *     id: id
     *     authCode: 授权码
     *     customer: {
     *         class: Customer
     *         id: customer id
     *         address: 地址
     *         company: {
     *             class: CustomerCompany
     *             id: company id
     *             companyName: 公司名
     *         }
     *     }
     * }
     * @param excelExport 注解对象
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2019/6/5 10:47
     **/
    private Map<String, Object> convertToTree(ExcelExport excelExport) {
        List<ExcelSerialize> ess = new LinkedList<>(Arrays.asList(excelExport.value()));
        List<Class> clazzList = ess.stream()
                .map(ExcelSerialize::clazz)
                .collect(Collectors.toList());
        Map<String, Object> map = matchField(ess, clazzList, 0);
        return map;

    }

    /**
     * 根据注解信息, 匹配字段名和表头信息
     * @param ess ExcelSerialize 列表
     * @param classList 类对象列表
     * @param index 当前待解析的注解对象在列表中的索引
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2019/6/5 14:05
     **/
    private Map<String, Object> matchField(List<ExcelSerialize> ess, List<Class> classList, int index) {
        classList.remove(index);
        ExcelSerialize es = ess.remove(index);
        Class clazz = es.clazz();
        String[] fields = es.fields();
        String[] heads = es.heads();
        Map<String, Object> curr = new LinkedHashMap<>(fields.length + 1);
        curr.put("class", clazz);

        if (fields.length == heads.length) {
            for (int i = 0; i < fields.length; i++) {
                curr.put(fields[i], heads[i]);
            }
            return curr;
        }

        // 解析复杂的内部属性
        for (int i = 0, hi = 0; i < fields.length; i++)  {
            PropertyDescriptor descriptor = MyBeanUtils.getPropertyDescriptor(clazz, fields[i]);
            if (descriptor == null) {
                throw new RuntimeException(String.format("属性不存在: %s#%s", clazz.getName(), fields[i]));
            }
            Class<?> propertyType = descriptor.getPropertyType();
            int firstIndex = classList.indexOf(propertyType);
            if (firstIndex < 0) {
                // 直接对应放进map
                try {
                    curr.put(fields[i], heads[hi++]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new RuntimeException("缺少表头信息: " + fields[i]);
                }
            } else {
                // 递归调用, 将返回的结果放进map
                Map<String, Object> nMap = matchField(ess, classList, firstIndex);
                curr.put(fields[i], nMap);
            }
        }
        return curr;
    }


    /**
     * 根据字段信息提取该字段在指定对象中的值
     * @param obj 对象
     * @param serialFieldMap 字段信息
     * @return java.util.List<java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2019/6/5 14:07
     **/
    private List<Object> resolve(Object obj, Map<String, Object> serialFieldMap)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class expectClass = (Class) serialFieldMap.get("class");
        // 如果开启了懒加载, 就透过hibernate代理, 取出真实对象
        obj = HibernateUtils.throughProxy(obj);
        Class<?> factClass = obj.getClass();
        if (factClass != expectClass) {
            throw new RuntimeException("excel导出失败, 类型无法匹配: "
                    + "\texpect class: " + expectClass.getName()
                    + "\tfact class: " + factClass.getName());
        }

        List<Object> retList = new LinkedList<>();
        List<List<Object>> nLists = new LinkedList<>();

        // 字段名列表
        List<String> fieldList = serialFieldMap.keySet().stream()
                .filter(s -> !"class".equals(s))
                .collect(Collectors.toList());

        for (String f : fieldList) {
            Object val = MyBeanUtils.getProperty(obj, f);
//            if (val == null) {
//                throw new RuntimeException(String.format("excel导出失败, %s#%s: 字段为null", factClass.getName(), f));
//            }
            if (serialFieldMap.get(f) instanceof Map) {
                Map m = (Map) serialFieldMap.get(f);
                List<Object> nList = resolve(val, m);
                nLists.add(nList);
            } else {
                retList.add(val);
            }
        }

        nLists.forEach(retList::addAll);

        return retList;
    }
}

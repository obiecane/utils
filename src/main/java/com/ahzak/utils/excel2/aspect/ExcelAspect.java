package com.ahzak.utils.excel2.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.ahzak.utils.BeanUtil;
import com.ahzak.utils.DateUtil;
import com.ahzak.utils.excel2.annotation.ExcelExport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

        List<Object> objectList = checkTarget(returnValue);
        if (objectList == null) {
            return returnValue;
        }

        try (
                OutputStream out = response.getOutputStream()
        ) {
            // 1. 根据注解解析List,
            final List<List<Object>> valuesList = fetchValuesList(objectList, excelExport);

            // 2. 处理文件名
            String excelBaseName = FilenameUtils.getBaseName(excelExport.name());
            String excelName = excelBaseName + ".xls";

            // 3. 写出Excel到输出流
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(excelName, "UTF-8"));
            this.genAndWriteExcel(Arrays.asList(excelExport.heads()), valuesList, excelBaseName, out);
        } catch (IOException e) {
            log.error("excel导出失败: {}#{}",
                    joinPoint.getTarget().getClass().getName(),
                    joinPoint.getSignature().getName(), e);
        }

        return returnValue;
    }


    /**
     * 只有返回list, 对象数组, 或者返回JcResult但是包装的data是list或对象数组才开始生成Excel
     *
     * @param returnValue
     * @return java.util.List<java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2020/3/14 14:33
     */
    private List<Object> checkTarget(Object returnValue) {
        Object retData = returnValue;
//        if (retData instanceof JcResult) {
//            retData = ((JcResult) returnValue).getData();
//        }
        if (ArrayUtil.isArray(retData)) {
            retData = Arrays.asList(retData);
        }
        if (!(retData instanceof List)) {
            return null;
        }
        return (List<Object>) retData;
    }

    /**
     * 提取填充excel的值
     *
     * @param objList 对象列表
     * @return java.util.List<java.util.List < java.lang.Object>>
     * @author Zhu Kaixiao
     * @date 2019/6/5 12:04
     **/
    private List<List<Object>> fetchValuesList(List<Object> objList, ExcelExport excelExport) {
        List<List<Object>> ret = new ArrayList<>(objList.size());
        final String[] fields = excelExport.fields();

        for (Object obj : objList) {
            List<Object> list = new ArrayList<>(fields.length);
            for (String fieldPath : fields) {
                final Object property = BeanUtil.getProperty(obj, fieldPath);
                list.add(property);
            }
            ret.add(list);
        }

        return ret;
    }


    /**
     * 生成excel并写出到输出流中
     *
     * @param headerList 表头
     * @param valueList  填充数据
     * @param sheetName  excel中的sheet页的名字
     * @param out        输出流
     * @author Zhu Kaixiao
     * @date 2020/3/14 14:20
     */
    private void genAndWriteExcel(List<String> headerList, List<List<Object>> valueList, String sheetName, OutputStream out)
            throws IOException {
        //第一步创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();

        //第二步创建sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        //第三步创建行row:添加表头0行
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        //居中
//        style.setAlignment(HorizontalAlignment.CENTER);

        //第四步创建表头单元格, 并设置表头数据
        for (int i = 0, size = headerList.size(); i < size; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(headerList.get(i));
            cell.setCellStyle(style);
        }

        //第五步插入数据
        for (int i = 0; i < valueList.size(); i++) {
            List<?> vals = valueList.get(i);
            row = sheet.createRow(i + 1);
            for (int j = 0; j < vals.size(); j++) {
                //创建单元格并且添加数据
                Object val = vals.get(j);
                if (val == null) {
                    val = "";
                } else if (val instanceof Date) {
                    val = DateUtil.formatDateTime((Date) val);
                } else if (val instanceof LocalDateTime) {
                    val = DateUtil.formatDateTime((LocalDateTime) val);
                } else if (val instanceof LocalDate) {
                    val = DateUtil.formatDate((LocalDate) val);
                } else if (val instanceof LocalTime) {
                    val = DateUtil.formatTime((LocalTime) val);
                }
                row.createCell(j).setCellValue(val.toString());
            }
        }

        //第六步将生成excel文件写出到输出流
        wb.write(out);
        out.close();
    }
}

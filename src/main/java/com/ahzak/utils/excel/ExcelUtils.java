package com.ahzak.utils.excel;

import com.ahzak.utils.date.MyDateUtils;
import org.apache.poi.hssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/6/4 8:35
 */
public class ExcelUtils {

    private ExcelUtils() {

    }

    public static void genAndWriteExcel(List<String> headerList, List<List<Object>> valueList, String sheetName, OutputStream out)
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

        //第四步创建单元格
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
                    val = MyDateUtils.formatDate((Date) val, MyDateUtils.COM_Y_M_D_H_M_S_PATTERN);
                }
                row.createCell(j).setCellValue(val.toString());
            }
        }

        //第六步将生成excel文件写出到输出流
        wb.write(out);
        out.close();
    }
}

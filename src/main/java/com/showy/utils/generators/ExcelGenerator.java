package com.showy.utils.generators;

import com.showy.utils.DateTimeUtil;
import com.showy.utils.ExcelUtil;
import com.showy.utils.model.CustomFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
public class ExcelGenerator {

    public static CustomFile writeExcel(List<Object[]> rowDataList) {
        return writeExcel(rowDataList, "file_" + DateTimeUtil.getCurrDate() + ".xlsx");
    }

    public static CustomFile writeExcel(List<Object[]> rowDataList, String fileName) {
        try {
            SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE /* 100 */);
            SXSSFSheet sheet = workbook.createSheet();

            ExcelUtil.writeRowsWithHeader(workbook, sheet, rowDataList);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();

            workbook.close();
            byteArrayOutputStream.close();

            return new CustomFile(fileName, bytes, "");
        } catch (Exception e) {
            log.error("Error in writeExcel(): ", e);
            return new CustomFile();
        }
    }
}

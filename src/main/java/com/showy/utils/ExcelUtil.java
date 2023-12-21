package com.showy.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static com.showy.utils.constant.GlobalConstant.EXCEL_EXPORTER_DATE_FORMAT;

public class ExcelUtil {

    public static void writeHeaderRow(SXSSFWorkbook workbook, SXSSFSheet sheet, Object[] headerNameArr) {
        SXSSFRow row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);

        style.setFont(font);

        ExcelUtil.setCellColor(style, IndexedColors.LIGHT_GREEN.getIndex(), FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headerNameArr.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(headerNameArr[i]));
            cell.setCellStyle(style);
        }
    }

    public static void writeRowsWithHeader(SXSSFWorkbook workbook, SXSSFSheet sheet, List<Object[]> rowDataArrList) {
        Object[] headerRowData = rowDataArrList.get(0);
        writeHeaderRow(workbook, sheet, headerRowData);

        short dateFormat = workbook
                .getCreationHelper()
                .createDataFormat()
                .getFormat(EXCEL_EXPORTER_DATE_FORMAT);

        CellStyle cellStyleForDate = workbook.createCellStyle();
        cellStyleForDate.setDataFormat(dateFormat);

        for (int i = 1; i < rowDataArrList.size(); i++) {
            SXSSFRow row = sheet.createRow(i);
            Object[] rowDataArr = rowDataArrList.get(i);

            for (int j = 0; j < rowDataArr.length; j++) {
                SXSSFCell cell = row.createCell(j);
                setCellValueFromObject(cell, rowDataArr[j], cellStyleForDate);
            }
        }

        autoSizeColumnsFromHeader(sheet, headerRowData);
    }

    public static void setCellColor(CellStyle cellStyle, short colorIndex, FillPatternType fillPatternType) {
        cellStyle.setFillForegroundColor(colorIndex);
        cellStyle.setFillPattern(fillPatternType);
    }

    public static void setCellValueFromObject(SXSSFCell cell, Object value, CellStyle cellStyleForDate) {
        try {
            if (value == null) {
                cell.setCellValue("");
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Calendar) {
                cell.setCellStyle(cellStyleForDate);
                cell.setCellValue((Calendar) value);
            } else if (value instanceof Timestamp) {
                cell.setCellStyle(cellStyleForDate);
                cell.setCellValue((Timestamp) value);
            } else {
                cell.setCellValue(value.toString());
            }
        } catch (Exception e) {
            cell.setCellValue("");
        }
    }

    public static void autoSizeColumnsFromHeader(SXSSFSheet sheet, Object[] headerRowData) {
        /*
        This is even with SXSSF workbook is quite an heavy operation and ends up increasing time by 200-400% than without it.
        //int columnSize = sheet.getRow(0).getPhysicalNumberOfCells();
        //autoSizeColumns(sheet, columnSize);

        Manually setting columnWidth from header(row0) results in far better time
        */
        for (int i = 0; i < headerRowData.length; i++) {
            int currRowWidth = (int) ((String.valueOf(headerRowData[i]).length() + 5) * 1.45f) * 256; // where 1.45 is a max character width for font and 256 font units.
            sheet.setColumnWidth(i, currRowWidth);
        }
    }

    public static void setStyleForRow(Workbook workbook, Row row, boolean hasBorder, boolean isBold, Short colorIndex) {
        CellStyle style = workbook.createCellStyle();

        if (hasBorder) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        style.setAlignment(HorizontalAlignment.CENTER);

        if (isBold) {
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
        }

        ExcelUtil.setCellColor(style, colorIndex, FillPatternType.SOLID_FOREGROUND);

        for (Cell cell : row) {
            cell.setCellStyle(style);
        }
    }

    public static void setStyleForCell(Workbook workbook, Cell cell, boolean hasBorder, boolean isBold, Short colorIndex) {
        CellStyle style = workbook.createCellStyle();

        if (hasBorder) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        style.setAlignment(HorizontalAlignment.CENTER);

        if (isBold) {
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
        }

        ExcelUtil.setCellColor(style, colorIndex, FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);
    }
}

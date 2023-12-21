package com.showy.utils.parser;

import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;
import com.showy.utils.ConversionUtils;
import com.showy.utils.DateTimeUtil;
import com.showy.utils.ObjectUtil;
import com.showy.utils.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static com.showy.utils.parser.FileParser.isHeaderValidForFile;

@Slf4j
public class ExcelParser {

    public static List<List<Object>> parse(byte[] fileByteArr, Map<Integer, String> columnNoToHeaderNameMap, String fileName) throws FileProcessingException {
        return parse(fileByteArr, columnNoToHeaderNameMap, fileName, false);
    }

    public static List<List<Object>> parse(byte[] fileByteArr, Map<Integer, String> columnNoToHeaderNameMap, String fileName, boolean parseHeaderRow) throws FileProcessingException {
        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileByteArr)) {
            return parse(fileInputStream, columnNoToHeaderNameMap, fileName, parseHeaderRow);
        } catch (FileProcessingException fpe) {
            throw fpe;
        } catch (Exception e) {
            log.error("Error in parse(): ", e);
            return null;
        }
    }

    public static List<List<Object>> parse(File file, Map<Integer, String> columnNoToHeaderNameMap, String fileName) throws FileProcessingException {
        return parse(file, columnNoToHeaderNameMap, fileName, false);
    }

    public static List<List<Object>> parse(File file, Map<Integer, String> columnNoToHeaderNameMap, String fileName, boolean parseHeaderRow) throws FileProcessingException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return parse(fileInputStream, columnNoToHeaderNameMap, fileName, parseHeaderRow);
        } catch (FileProcessingException fpe) {
            throw fpe;
        } catch (Exception e) {
            log.error("Error in parse(): ", e);
            return null;
        }
    }

    public static List<List<Object>> parse(InputStream fileInputStream, Map<Integer, String> columnNoToHeaderNameMap,
                                           String fileName) throws FileProcessingException {
        return parse(fileInputStream, columnNoToHeaderNameMap, fileName, false);
    }

    public static List<List<Object>> parse(InputStream fileInputStream, Map<Integer, String> columnNoToHeaderNameMap,
                                           String fileName, boolean parseHeaderRow) throws FileProcessingException {
        List<List<Object>> parsedData;
        // Change done to enable SAX Parsing for XLSX, for lower memory consumption (it does create temporary file locally for unzipping excel).
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
        ZipInputStreamZipEntrySource.setThresholdBytesForTempFiles(16_384); //16kb
        ZipPackage.setUseTempFilePackageParts(true);
        try (Workbook workbook = (fileName.endsWith(".xlsx")) ? StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(2048)
                .setSharedStringsImplementationType(SharedStringsImplementationType.TEMP_FILE_BACKED)
                .open(fileInputStream) : WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            parsedData = new ArrayList<>();
            for (Row row : sheet) {
                List<Object> rowData = new ArrayList<>();
                IntStream.range(0, row.getLastCellNum()).forEach(i -> {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        rowData.add(cell.getRichStringCellValue().getString());
                    } else if (cellType == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            rowData.add(DateTimeUtil.dateToPatternStr(date, "yyyy-MM-dd HH:mm:ss"));
                        } else {
                            BigDecimal bigDecimal = BigDecimal.valueOf(cell.getNumericCellValue());
                            String strippedNumber = bigDecimal.stripTrailingZeros().toPlainString();
                            if (strippedNumber.contains(".")) {
                                rowData.add(ConversionUtils.toType(strippedNumber, Double::parseDouble, 0D));
                            } else {
                                rowData.add(ConversionUtils.toType(strippedNumber, Integer::parseInt, 0L));
                            }
                        }
                    } else if (cellType == CellType.BOOLEAN) {
                        rowData.add(String.valueOf(cell.getBooleanCellValue()));
                    } else if (cellType == CellType.FORMULA) {
                        CellType cachedFormulaResultType = cell.getCachedFormulaResultType();
                        if (cachedFormulaResultType == CellType.NUMERIC) {
                            BigDecimal bigDecimal = BigDecimal.valueOf(cell.getNumericCellValue());
                            String strippedNumber = bigDecimal.stripTrailingZeros().toPlainString();
                            if (strippedNumber.contains(".")) {
                                rowData.add(ConversionUtils.toType(strippedNumber, Double::parseDouble, 0D));
                            } else {
                                rowData.add(ConversionUtils.toType(strippedNumber, Integer::parseInt, 0));
                            }
                        } else {
                            rowData.add(cell.getStringCellValue());
                        }
                    }
                });

                /** Matching headerNameMap to first Row of file **/
                if (row.getRowNum() == 0) {
                    if (ObjectUtil.notNullOrEmpty(columnNoToHeaderNameMap)) {
                        isHeaderValidForFile(rowData, columnNoToHeaderNameMap);
                    }
                    if (parseHeaderRow) {
                        parsedData.add(rowData);
                    }
                    continue;
                }

                parsedData.add(rowData);
            }
            return parsedData;
        } catch (FileProcessingException fpe) {
            throw fpe;
        } catch (Exception e) {
            log.error("Exception in readExcel() : ", e);
            throw new FileProcessingException(e.getMessage());
        }
    }
}

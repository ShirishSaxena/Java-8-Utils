package com.showy.utils.parser;

import com.showy.utils.constant.FileTypes;
import com.showy.utils.ObjectUtil;
import com.showy.utils.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FileParser {

    public static List<String[]> parse(InputStream stream, Map<Integer, String> colNoToHeaderNameMap, String fileName, FileTypes fileTypes) throws FileProcessingException {
        try {
            if (fileTypes == FileTypes.CSV) {
                return CsvParser.readCsv(stream, colNoToHeaderNameMap);
            } else if (fileTypes == FileTypes.XLS || fileTypes == FileTypes.XLSX) {
                List<List<Object>> parse = ExcelParser.parse(stream, colNoToHeaderNameMap, fileName);
                return parse.stream()
                        .map(o -> o.stream()
                                .map(Object::toString)
                                .toArray(String[]::new))
                        .collect(Collectors.toList());
            }
            throw new FileProcessingException("FileType not supported");
        } catch (FileProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in parse():", e);
            throw new FileProcessingException("Exception: " + e.getMessage());
        }
    }

    public static void isHeaderValidForFile(String[] rowData, Map<Integer, String> colNoToHeaderNameMap) throws FileProcessingException {
        List<String> collect = Arrays.stream(rowData).collect(Collectors.toList());
        isHeaderValidForFile(collect, colNoToHeaderNameMap);
    }

    public static <E> void isHeaderValidForFile(List<E> rowData, Map<Integer, String> colNoToHeaderNameMap) throws FileProcessingException {
        if (ObjectUtil.isNull(rowData) || ObjectUtil.isNullOrEmpty(colNoToHeaderNameMap)) {
            throw new FileProcessingException("Invalid headerRow or colNoToHeaderNameMap");
        }

        Map<String, Object> errorMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entrySet : colNoToHeaderNameMap.entrySet()) {
            int position = entrySet.getKey();
            String value = entrySet.getValue().trim();

            if (position >= rowData.size()) {
                throw new FileProcessingException("No Column exist for colNo: " + position + " [" + value + "]");
            }

            Object colValue = rowData.get(position);
            if (ObjectUtil.isNull(colValue) ||
                    !value.equalsIgnoreCase(colValue.toString().trim())) {
                errorMap.put(value, colValue);
            }
        }

        if (!errorMap.isEmpty()) {
            throw new FileProcessingException("Invalid headers: " + errorMap);
        }
    }
}

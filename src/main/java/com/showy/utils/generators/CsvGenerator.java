package com.showy.utils.generators;

import com.opencsv.CSVWriter;
import com.showy.utils.DateTimeUtil;
import com.showy.utils.model.CustomFile;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CsvGenerator {

    public static CustomFile writeCsv(List<Object[]> rowDataList) {
        List<String[]> collect = rowDataList.stream().map(objects -> Arrays.stream(objects).map(Object::toString).toArray(String[]::new)).collect(Collectors.toList());
        return writeCsv(collect, "file_" + DateTimeUtil.getCurrDate() + ".csv");
    }

    public static CustomFile writeCsv(List<String[]> rowDataList, String fileName) {
        return writeCsv(rowDataList, fileName, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER);
    }

    public static CustomFile writeCsv(List<String[]> rowDataList, String fileName, Character delimiter, Character defaultQuoteChar) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(byteArrayOutputStream),
                    (delimiter == null) ? CSVWriter.DEFAULT_SEPARATOR : delimiter,
                    (defaultQuoteChar == null) ? CSVWriter.DEFAULT_QUOTE_CHARACTER : defaultQuoteChar,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            csvWriter.writeAll(rowDataList);
            csvWriter.flush();

            byte[] bytes = byteArrayOutputStream.toByteArray();
            csvWriter.close();

            return new CustomFile(fileName, bytes, "");
        } catch (Exception e) {
            log.error("Error in writeCsv(): ", e);
            return new CustomFile();
        }
    }
}

package com.showy.utils.parser;

import com.opencsv.*;
import com.showy.utils.ObjectUtil;
import com.showy.utils.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static com.showy.utils.parser.FileParser.isHeaderValidForFile;

@Slf4j
public class CsvParser {

    public static List<String[]> readCsv(byte[] bytes) throws FileProcessingException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            return readCsv(byteArrayInputStream, null, null, true, false);
        } catch (IOException e) {
            throw new FileProcessingException("I/O Exception");
        }
    }

    public static List<String[]> readCsv(InputStream file) throws FileProcessingException {
        return readCsv(file, null, null, true, false);
    }

    public static List<String[]> readCsv(InputStream file, Map<Integer, String> columnNoToHeaderNameMap) throws FileProcessingException {
        return readCsv(file, null, columnNoToHeaderNameMap, true, false);
    }

    public static List<String[]> readCsv(InputStream file, boolean parseHeaderRow) throws FileProcessingException {
        return readCsv(file, null, null, true, parseHeaderRow);
    }


    public static List<String[]> readCsv(InputStream file, Character customDelimiter, Map<Integer, String> columnNoToHeaderNameMap, boolean ignoreQuotations, boolean parseHeaderRow) throws FileProcessingException {
        try (InputStreamReader reader = new InputStreamReader(file)) {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(customDelimiter == null ? CSVWriter.DEFAULT_SEPARATOR : customDelimiter)
                    .withIgnoreQuotations(ignoreQuotations)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .build();

            /** Matching headerNameMap to first Row of file **/
            String[] fileFirstRow = csvReader.peek();
            if (ObjectUtil.notNullOrEmpty(columnNoToHeaderNameMap)) {
                isHeaderValidForFile(fileFirstRow, columnNoToHeaderNameMap);
            }

            if (parseHeaderRow) {
                csvReader.skip(1);
            }

            List<String[]> parsedData = csvReader.readAll();
            csvReader.close();
            return parsedData;
        } catch (FileProcessingException fileProcessingException) {
            throw fileProcessingException;
        } catch (Exception e) {
            log.error("Exception in readCSV() : ", e);
            throw new FileProcessingException("Error parsing: " + e.getMessage());
        }
    }

}

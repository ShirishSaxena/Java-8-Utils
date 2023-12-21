package com.showy.utils.parser;

import com.showy.utils.DateTimeUtil;
import com.showy.utils.ObjectUtil;
import com.showy.utils.exception.DataParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.Errors;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DataParser {

    static Converter<String, Timestamp> CUSTOM_STRING_TO_TIMESTAMP_CONVERTER = mappingContext -> {
        String source = mappingContext.getSource();
        if (StringUtils.isBlank(source)) {
            return null;
        }
        // Try default parser
        Timestamp timestamp = DateTimeUtil.getTimestampFromString(source, null);
        if (timestamp == null) {
            for (DateFormat dateFormat : DateTimeUtil.DATE_FORMAT_LIST_FOR_INVOICE_UPLOADER) {
                timestamp = DateTimeUtil.getTimestampFromString(source, dateFormat);
                if (timestamp != null) {
                    return timestamp;
                }
            }
            throw new Errors().errorMapping(mappingContext.getSourceType(), mappingContext.getDestinationType())
                    .addMessage("Can not convert (" + source + ") to Timestamp (custom formatter)")
                    .toMappingException();
        }
        return timestamp;
    };

    public static <T> T genericParser(String[] rowData, Map<String, Integer> objToColNoMap, Class<T> className, ModelMapper modelMapper) throws DataParseException {
        return genericParser(rowData, objToColNoMap, className, modelMapper, null);
    }

    public static <T> T genericParser(String[] rowData, Map<String, Integer> objToColNoMap, Class<T> className, ModelMapper modelMapper, Map<String, String> extraFieldNameToDataMappings) throws DataParseException {
        try {
            Map<String, String> parsedGenericClassMap = objToColNoMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue() < rowData.length)
                    .filter(e -> ObjectUtil.notNullOrEmpty(rowData[e.getValue()]))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> rowData[e.getValue()], (oldV, newV) -> newV));

            if (ObjectUtil.notNullOrEmpty(extraFieldNameToDataMappings)) {
                parsedGenericClassMap.putAll(extraFieldNameToDataMappings);
            }

            return modelMapper.map(parsedGenericClassMap, className);
        } catch (Exception e) {
            log.error("Exception in genericParser() for objName {} for row {} : ", className.getName(), rowData, e);
            throw new DataParseException("Error parsing: " + Arrays.toString(rowData) + " [" + e.getMessage() + "]");
        }
    }

    public static <T> List<T> genericParser(List<String[]> parsedData, Map<String, Integer> objToColNoMap, Class<T> className, boolean useCustomStringToTimestampConverter) throws DataParseException {
        return genericParser(parsedData, objToColNoMap, className, useCustomStringToTimestampConverter, new HashMap<>());
    }

    public static <T> List<T> genericParser(List<String[]> parsedData, Map<String, Integer> objToColNoMap, Class<T> className,
                                            boolean useCustomStringToTimestampConverter, Map<String, String> extraFieldNameToDataMappings) throws DataParseException {
        List<T> genericObjList = new ArrayList<>();
        int start = 0;
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            if (useCustomStringToTimestampConverter) {
                modelMapper.createTypeMap(String.class, Timestamp.class).setConverter(CUSTOM_STRING_TO_TIMESTAMP_CONVERTER);
            }

            long startTime = System.currentTimeMillis();
            for (; start < parsedData.size(); start++) {
                T t = genericParser(parsedData.get(start), objToColNoMap, className, modelMapper, extraFieldNameToDataMappings);
                genericObjList.add(t);
            }

            log.debug("RowDataList to Object({}) Mapping time : {}",
                    className.getName(), String.format("%.3f s", (double) (System.currentTimeMillis() - startTime) / 1000));
            return genericObjList;
        } catch (DataParseException dataParseException) {
            throw dataParseException;
        } catch (Exception e) {
            log.error("Exception in genericParser() for objName {} on rowNo {} : ", className.getName(), start, e);
            throw new DataParseException("Error on RowNo: " + start + " [" + e.getMessage() + "]");
        }
    }
}

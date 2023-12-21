package com.showy.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConversionUtilsTests {

    static final List<TestObject> testObjectList = Arrays.asList(
            new TestObject(1, "test1", "val654"),
            new TestObject(2, "test2", "val111"),
            new TestObject(3, "test3", "val9"),
            new TestObject(4, "test4", "val4"),
            new TestObject(5, "test5", "val4"),
            new TestObject(6, "test6", null)
    );

    static final int testListSize = testObjectList.size();
    static final int value1CheckSize = testObjectList.size();
    static final int value2DupCheckSize = testObjectList.size() - 1;

    @Test
    void checkToMap() {
        int idMapSize = ConversionUtils.toMap(testObjectList, null, TestObject::getId).size();
        int value1MapSize = ConversionUtils.toMap(testObjectList, null, TestObject::getValue1).size();
        int value2MapSize = ConversionUtils.toMap(testObjectList, null, TestObject::getValue2).size();

        assertThat(idMapSize).isEqualTo(testListSize);
        assertThat(value1MapSize).isEqualTo(value1CheckSize);
        assertThat(value2MapSize).isEqualTo(value2DupCheckSize);
    }

    @Test
    void checkListToSet() {
        int idSetSize = ConversionUtils.toSet(testObjectList, null, TestObject::getId).size();
        int value1SetSize = ConversionUtils.toSet(testObjectList, null, TestObject::getValue1).size();
        int value2SetSize = ConversionUtils.toSet(testObjectList, null, TestObject::getValue2).size();

        assertThat(idSetSize).isEqualTo(testListSize);
        assertThat(value1SetSize).isEqualTo(value1CheckSize);
        assertThat(value2SetSize).isEqualTo(value2DupCheckSize);
    }

    @Test
    void checkListToMapOfList() {
        int value1MapOfListSize = ConversionUtils.toMapOfList(testObjectList, null, TestObject::getValue1).size();
        int value2MapOfListSize = ConversionUtils.toMapOfList(testObjectList, t -> t.getValue2() != null, TestObject::getValue2).size();

        assertThat(value1MapOfListSize).isEqualTo(testListSize);
        assertThat(value2MapOfListSize).isEqualTo(4);
    }

    @Test
    void checkJsonStringToMapConversion() {
        String validString = "{" +
                "    \"1\": \"One\"," +
                "    \"2\": \"Two\"," +
                "    \"3\": \"Three\"" +
                "}";

        String invalidJson = "{{{}";
        int validMapSize = 3;

        Map<String, String> stringStringMap = ConversionUtils.toMap(validString);
        Map<String, String> invalidStringMap = ConversionUtils.toMap(invalidJson);

        assertThat(invalidStringMap.size()).isEqualTo(0);
        assertThat(stringStringMap).isNotNull();
        assertThat(stringStringMap.size()).isEqualTo(validMapSize);
    }

    @Test
    void checkJsonStringToMapOfMapConversion() {
        String validString = "{" +
                "    \"1\": {" +
                "        \"value\": \"One\"," +
                "        \"one\": \"1\"" +
                "    },\n" +
                "    \"2\": {" +
                "        \"value\": \"Two\"," +
                "        \"two\": \"2\"" +
                "    }}";
        String invalidJson = "{{{}";
        String validEmptyJson = "{}";
        int validMapSize = 2;

        TypeReference<Map<String, Map<String, String>>> typeReference = new TypeReference<Map<String, Map<String, String>>>() {
        };
        Map<String, Map<String, String>> validMapOfMap = ConversionUtils.toTypeReference(validString, typeReference);
        Map<String, Map<String, String>> invalidStringMap = ConversionUtils.toTypeReference(invalidJson, typeReference);
        Map<String, Map<String, String>> validEmptyMapOfMap = ConversionUtils.toTypeReference(validEmptyJson, typeReference);

        assertThat(invalidStringMap).isNull();
        assertThat(validEmptyMapOfMap).isNotNull();
        assertThat(validEmptyMapOfMap.size()).isEqualTo(0);
        assertThat(validMapOfMap).isNotNull();
        assertThat(validMapOfMap.size()).isEqualTo(validMapSize);
    }

    @Test
    void checkJsonStringToObjectConversion() {
        String jsonStrFromList = ConversionUtils.toJsonString(testObjectList);


        List<TestObject> convertedTestObjList = ConversionUtils.toTypeReference(jsonStrFromList, new TypeReference<List<TestObject>>() {
        });

        assertThat(jsonStrFromList).isNotNull();
        assertThat(convertedTestObjList).isNotNull();
        assertThat(convertedTestObjList.size()).isEqualTo(testListSize);
    }

    @Test
    void checkTypeToDifferentTypeConversion() {
        String intStr = "5656356";
        String doubleStr = "5656356.00233";
        Integer iVal = ConversionUtils.toType(intStr, Integer::parseInt, 0);
        Double dVal = ConversionUtils.toType(doubleStr, Double::parseDouble, 0D);
        Integer invalidIntVal = ConversionUtils.toType("INVALID STRING", Integer::parseInt, 0);

        int intValFromDoubleStr = ConversionUtils.toType(doubleStr, v -> Double.valueOf(v).intValue(), 0);

        assertThat(iVal).isEqualTo(5656356);
        assertThat(dVal).isEqualTo(5656356.00233);
        assertThat(invalidIntVal).isEqualTo(0);
        assertThat(intValFromDoubleStr).isEqualTo(5656356);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestObject {
        private int id;
        private String value1;
        private String value2;
    }
}

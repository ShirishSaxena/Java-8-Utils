package com.showy.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class ObjectUtil {
    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> boolean isNullOrEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNullOrEmpty(String... strings) {
        return strings == null || strings.length == 0 || Arrays.stream(strings).allMatch(ObjectUtil::isNullOrEmpty);
    }

    public static <E> boolean isNullOrDefault(E value, E defaultVal) {
        return value == null || defaultVal == value;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static <T> boolean notNullOrEmpty(Collection<T> collection) {
        return !isNullOrEmpty(collection);
    }

    public static <K, V> boolean notNullOrEmpty(Map<K, V> map) {
        return !isNullOrEmpty(map);
    }

    public static boolean notNullOrEmpty(String string) {
        return !isNullOrEmpty(string);
    }

    public static boolean notNullOrEmpty(String... strings) {
        return !isNullOrEmpty(strings);
    }

    public static <E> boolean notNullOrDefault(E value, E defaultVal) {
        return !isNullOrDefault(value, defaultVal);
    }

    public static boolean notNull(Object obj) {
        return !isNull(obj);
    }
}

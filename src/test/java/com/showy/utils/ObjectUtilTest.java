package com.showy.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ObjectUtilTest {

    @Test
    void checkForCollection() {
        List<Integer> list = Arrays.asList(2, 3, 56, 6777);
        assertThat(ObjectUtil.isNullOrEmpty(list)).isEqualTo(false);
        assertThat(ObjectUtil.notNullOrEmpty(list)).isEqualTo(true);

        HashSet<Integer> integers = new HashSet<>();
        assertThat(ObjectUtil.isNullOrEmpty(integers)).isEqualTo(true);
        assertThat(ObjectUtil.notNullOrEmpty(integers)).isEqualTo(false);

        list = null;
        assertThat(ObjectUtil.isNullOrEmpty(list)).isEqualTo(true);
        assertThat(ObjectUtil.notNullOrEmpty(list)).isEqualTo(false);

        assertThat(ObjectUtil.isNull(list)).isEqualTo(true);
        assertThat(ObjectUtil.notNull(list)).isEqualTo(false);
    }

    void checkForObject() {
        Integer nn = 423;

        assertThat(ObjectUtil.isNull(nn)).isEqualTo(false);
        assertThat(ObjectUtil.notNull(nn)).isEqualTo(true);

        nn = null;
        assertThat(ObjectUtil.isNull(nn)).isEqualTo(true);
        assertThat(ObjectUtil.notNull(nn)).isEqualTo(false);
    }

    void checkForMap() {
        Map<Integer, Integer> map = new HashMap<>();
        assertThat(ObjectUtil.isNullOrEmpty(map)).isEqualTo(true);
        assertThat(ObjectUtil.notNullOrEmpty(map)).isEqualTo(false);

        assertThat(ObjectUtil.isNull(map)).isEqualTo(false);

        map.put(1, 222);
        assertThat(ObjectUtil.isNullOrEmpty(map)).isEqualTo(true);
        assertThat(ObjectUtil.notNullOrEmpty(map)).isEqualTo(false);
    }

    void checkForString() {
        String[] strings = {"1232", null, "23213"};

        assertThat(ObjectUtil.isNullOrEmpty(strings)).isEqualTo(true);
        assertThat(ObjectUtil.isNullOrEmpty(strings[1])).isEqualTo(false);

        assertThat(ObjectUtil.notNullOrEmpty(strings[1])).isEqualTo(true);

        strings[1] = "something";
        assertThat(ObjectUtil.notNullOrEmpty(strings)).isEqualTo(false);
    }

    void checkForDefault() {
        Double l = 0D;
        assertThat(ObjectUtil.notNullOrDefault(l, 0D)).isEqualTo(true);

        l = null;
        assertThat(ObjectUtil.isNullOrDefault(l, 0D)).isEqualTo(true);

        l = 4.23D;
        assertThat(ObjectUtil.notNullOrDefault(l, 4.23D)).isEqualTo(false);
    }
}
package com.showy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public class DateTimeUtil {

    public static DateFormat DATE_FORMAT_YYYY_MM_DD_HH = new SimpleDateFormat("yyyyMMdd_HH");
    public static DateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    // Might need to add more in future
    public static List<DateFormat> DATE_FORMAT_LIST_FOR_INVOICE_UPLOADER = Arrays.asList(
            new SimpleDateFormat("dd.MM.yyyy"), new SimpleDateFormat("dd-MM-yyyy"));


    public static String getTimestampStrFromPattern(String timestampPattern, boolean subtractOneHour) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (subtractOneHour) {
                calendar.add(Calendar.HOUR_OF_DAY, -1);
            }

            if (StringUtils.isBlank(timestampPattern)) {
                return DATE_FORMAT_YYYY_MM_DD_HH.format(calendar.getTime());
            } else {
                return new SimpleDateFormat(timestampPattern.trim()).format(calendar.getTime());
            }
        } catch (Exception e) {
            log.error("Exception in getFilePrefixFromPattern() : ", e);
        }
        return "YYYYMMDD_HH";
    }

    public static Date getCurrDateTime() {
        return Calendar.getInstance().getTime();
    }

    public static String getCurrDate() {
        Calendar newDateTime = Calendar.getInstance();
        newDateTime.set(Calendar.HOUR_OF_DAY, 0);
        newDateTime.set(Calendar.MINUTE, 0);
        newDateTime.set(Calendar.SECOND, 0);
        newDateTime.set(Calendar.MILLISECOND, 0);
        return DATE_FORMAT_YYYY_MM_DD.format(newDateTime.getTime());
    }

    public static String getDayBeforeDate() {
        Calendar newDateTime = Calendar.getInstance();
        newDateTime.add(Calendar.DATE, -1);
        newDateTime.set(Calendar.HOUR_OF_DAY, 0);
        newDateTime.set(Calendar.MINUTE, 0);
        newDateTime.set(Calendar.SECOND, 0);
        newDateTime.set(Calendar.MILLISECOND, 0);
        return DATE_FORMAT_YYYY_MM_DD.format(newDateTime.getTime());
    }

    public static Timestamp getCurrTimestamp() {
        return Timestamp.from(Instant.now());
    }

    public static String timestampToDateStr(Timestamp timestamp) {
        String dateWithoutTime = "";
        try {
            if (timestamp == null) {
                return dateWithoutTime;
            }

            Date date = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateWithoutTime = sdf.format(date);
        } catch (Exception e) {
            log.error("Error in timestampToDateStr() : ", e);
        }
        return dateWithoutTime;
    }

    public static String timestampStrToCustomPattern(String timestampStr, String patternToChange) {
        String dateWithoutTime = "";
        try {
            if (StringUtils.isBlank(timestampStr)) {
                return dateWithoutTime;
            }

            Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestampStr);
            return new SimpleDateFormat(patternToChange).format(d1);
        } catch (Exception e) {
            log.error("Error in timestampToDateStr() : ", e);
        }
        return dateWithoutTime;
    }

    public static String timestampStrToCustomPattern(Timestamp timestamp, String patternToChange) {
        return timestamp == null ? "" : timestampStrToCustomPattern(timestamp.toString(), patternToChange);
    }

    public static String dateToPatternStr(Date date, String patternToChange) {
        String customDateStr = "";
        try {
            if (date == null) {
                return customDateStr;
            }

            return new SimpleDateFormat(patternToChange).format(date);
        } catch (Exception e) {
            log.error("Error in dateToPatternStr() : ", e);
        }
        return customDateStr;
    }

    public static String dateTimeStrToCustomPattern(String dateTimeStr, String patternToParseFrom, String patternToChange) {
        String dateWithoutTime = "";
        try {
            if (StringUtils.isBlank(dateTimeStr)) {
                return dateWithoutTime;
            }

            Date d1 = new SimpleDateFormat(patternToParseFrom).parse(dateTimeStr);
            return new SimpleDateFormat(patternToChange).format(d1);
        } catch (Exception e) {
            log.error("Error in dateTimeStrToCustomPattern() : {}", dateTimeStr);
        }
        return dateWithoutTime;
    }

    public static Calendar parseStringToCalendar(String dateTimeStr, String patternToParseFrom) {
        Calendar cal = null;
        try {
            Date d = new SimpleDateFormat(patternToParseFrom).parse(dateTimeStr);
            cal = Calendar.getInstance();
            cal.setTime(d);
        } catch (ParseException ignored) {
        }
        return cal;
    }

    public static Timestamp getTimestampFromString(String toParse, DateFormat dateFormat) {
        try {
            if (dateFormat == null) {
                return Timestamp.valueOf(toParse);
            }

            return Timestamp.from(dateFormat.parse(toParse).toInstant());
        } catch (IllegalArgumentException | ParseException e) {
            return null;
        }
    }
}
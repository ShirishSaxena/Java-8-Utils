package com.showy.utils.constant;

public class GlobalConstant {
    public static final String PROPERTY_FIELD_FOR_SCHEDULER = "schedule.enable";
    public static final String SCHEDULER = "SCHEDULER"; // Use for @ConditionalOnInstance()
    public static final String NO_SCHEDULER = "NO-SCHEDULER"; // Use for @ConditionalOnInstance()
    public static final String PROPERTY_FIELD_FOR_SERVER_LOCATION = "spring.auth.server-type";

    public static final String EXCEL_EXPORTER_DATE_FORMAT = "dd/MMM/yyyy";

    public static String getTimeElapsedInSeconds(long startTime) {
        return String.format("%.3f s", (double) (System.currentTimeMillis() - startTime) / 1000);
    }
}

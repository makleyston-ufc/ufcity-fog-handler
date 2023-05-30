package com.ufcity.handler.procedures;

import java.util.HashMap;
import java.util.Map;

public class METHODS {

    public static final Map<String, Character> methodMap = new HashMap<>();

    static {
        methodMap.put("NONE", 'A');

        methodMap.put("FIXED_SIZE_GROUPING", 'B');
        methodMap.put("HAPPENS_FIRST_GROUPING", 'C');
        methodMap.put("AT_LEAST_TIME_GROUPING", 'D');
        methodMap.put("AT_LEAST_TIME_AND_SIZE_GROUPING", 'E');

        methodMap.put("MEAN_AGGREGATION_METHOD", 'F');
        methodMap.put("MEDIAN_AGGREGATION_METHOD", 'G');
        methodMap.put("MAX_AGGREGATION_METHOD", 'H');
        methodMap.put("MIN_AGGREGATION_METHOD", 'I');

        methodMap.put("IQR_REMOVE_OUTLIERS_METHOD", 'J');
        methodMap.put("PERCENTILE_REMOVE_OUTLIERS_METHOD", 'K');
        methodMap.put("TUKEY_REMOVE_OUTLIERS_METHOD", 'L');
        methodMap.put("Z_SCORE_REMOVE_OUTLIERS_METHOD", 'M');

        methodMap.put("MEAN_MISSING_DATA_METHOD", 'N');
        methodMap.put("MEDIAN_MISSING_DATA_METHOD", 'O');
        methodMap.put("LOCF_MISSING_DATA_METHOD", 'P');
        methodMap.put("INTERPOLATION_MISSING_DATA_METHOD", 'Q');
        methodMap.put("NOCB_MISSING_DATA_METHOD", 'R');
        methodMap.put("MODE_MISSING_DATA_METHOD", 'S');
    }

//    public static final char NONE = 'Z';
//
//    /* Grouping Data */
//    public static final char FIXED_SIZE_GROUPING = 'A';
//    public static final char HAPPENS_FIRST_GROUPING = 'B';
//    public static final char AT_LEAST_TIME_GROUPING = 'C';
//    public static final char AT_LEAST_TIME_AND_SIZE_GROUPING = 'D';
//
//    /* Data Aggregation */
//    public static final char MEAN_AGGREGATION_METHOD = 'E';
//    public static final char MEDIAN_AGGREGATION_METHOD = 'F';
//    public static final char MAX_AGGREGATION_METHOD = 'G';
//    public static final char MIN_AGGREGATION_METHOD = 'H';
//
//    /* Removing Outliers */
//    public static final char IQR_REMOVE_OUTLIERS_METHOD = 'I';
//    public static final char PERCENTILE_REMOVE_OUTLIERS_METHOD = 'J';
//    public static final char TUKEY_REMOVE_OUTLIERS_METHOD = 'K';
//    public static final char Z_SCORE_REMOVE_OUTLIERS_METHOD = 'L';
//
//    /* Missing Data */
//    public static final char MEAN_MISSING_DATA_METHOD = 'M';
//    public static final char MEDIAN_MISSING_DATA_METHOD = 'N';
//    public static final char LOCF_MISSING_DATA_METHOD = 'O';
//    public static final char INTERPOLATION_MISSING_DATA_METHOD = 'P';
//    public static final char NOCB_MISSING_DATA_METHOD = 'Q';
//    public static final char MODE_MISSING_DATA_METHOD = 'R';

}

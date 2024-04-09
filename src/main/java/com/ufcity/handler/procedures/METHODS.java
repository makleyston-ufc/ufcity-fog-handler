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

}

package com.ufcity.handler.procedures;

import java.util.*;

public class MissingDataHandling {

    private static MissingDataHandling instance = null;

    private char method;

    public char getMethod() {
        return method;
    }

    private MissingDataHandling(){}

    public static MissingDataHandling getInstance(){
        if(instance == null){
            instance = new MissingDataHandling();
        }
        return instance;
    }

    public void setMethod(char method) {
        this.method = method;
    }

    public List<Double> getMissingDataHandled(List<Double> data){
        if(this.method == METHODS.methodMap.get("MEAN_MISSING_DATA_METHOD")){
            return meanMissingData(data);
        } else if (this.method == METHODS.methodMap.get("MEDIAN_MISSING_DATA_METHOD")) {
            return medianMissingData(data);
        } else if (this.method == METHODS.methodMap.get("LOCF_MISSING_DATA_METHOD")) {
            return locfMissingData(data);
        } else if (this.method == METHODS.methodMap.get("INTERPOLATION_MISSING_DATA_METHOD")) {
            return interpolationMissingData(data);
        } else if (this.method == METHODS.methodMap.get("NOCB_MISSING_DATA_METHOD")) {
            return nocbMissingData(data);
        } else if (this.method == METHODS.methodMap.get("MODE_MISSING_DATA_METHOD")) {
            return modeMissingData(data);
        }
        return new ArrayList<>();
    }

    private List<Double> meanMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);
        double mean = calculateMean(data);

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                filledData.set(i, mean);
            }
        }

        return filledData;
    }

    private double calculateMean(List<Double> data) {
        double sum = 0.0;
        int count = 0;
        for (Double value : data) {
            if (value != null) {
                sum += value;
                count++;
            }
        }
        return sum / count;
    }

    private List<Double> medianMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                filledData.set(i, calculateMedian(filledData));
            }
        }

        return filledData;
    }

    private double calculateMedian(List<Double> data) {
        List<Double> sortedData = new ArrayList<>(data);
        sortedData.removeIf(value -> value == null);
        Collections.sort(sortedData);

        int size = sortedData.size();
        if (size % 2 == 0) {
            int midIndex = size / 2;
            return (sortedData.get(midIndex - 1) + sortedData.get(midIndex)) / 2.0;
        } else {
            int midIndex = size / 2;
            return sortedData.get(midIndex);
        }
    }

    private List<Double> locfMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);
        Double lastValue = null;

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                if (lastValue != null) {
                    filledData.set(i, lastValue);
                }
            } else {
                lastValue = filledData.get(i);
            }
        }

        return filledData;
    }

    private List<Double> interpolationMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                int prevIndex = findPreviousNonMissingValueIndex(filledData, i);
                int nextIndex = findNextNonMissingValueIndex(filledData, i);

                if (prevIndex != -1 && nextIndex != -1) {
                    double prevValue = filledData.get(prevIndex);
                    double nextValue = filledData.get(nextIndex);
                    double interpolatedValue = interpolate(prevValue, nextValue, prevIndex, nextIndex, i);
                    filledData.set(i, interpolatedValue);
                }
            }
        }

        return filledData;
    }

    private int findNextNonMissingValueIndex(List<Double> data, int currentIndex) {
        for (int i = currentIndex + 1; i < data.size(); i++) {
            if (data.get(i) != null) {
                return i;
            }
        }
        return -1;
    }

    private double interpolate(double x0, double x1, double y0, double y1, double x) {
        return y0 + (y1 - y0) * ((x - x0) / (x1 - x0));
    }

    private List<Double> modeMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);
        Map<Double, Integer> valueCounts = new HashMap<>();

        for (Double value : filledData) {
            if (value != null) {
                valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
            }
        }

        int maxCount = 0;
        Double modeValue = null;
        for (Map.Entry<Double, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                modeValue = entry.getKey();
            }
        }

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                filledData.set(i, modeValue);
            }
        }

        return filledData;
    }

    private List<Double> nocbMissingData(List<Double> data) {
        List<Double> filledData = new ArrayList<>(data);

        for (int i = 0; i < filledData.size(); i++) {
            if (filledData.get(i) == null) {
                int prevIndex = findPreviousNonMissingValueIndex(filledData, i);
                int nextIndex = findNextNonMissingValueIndex(filledData, i);

                if (prevIndex != -1 && nextIndex != -1) {
                    double prevValue = filledData.get(prevIndex);
                    double nextValue = filledData.get(nextIndex);
                    double nocbValue = nocb(prevValue, nextValue);
                    filledData.set(i, nocbValue);
                }
            }
        }

        return filledData;
    }

    private int findPreviousNonMissingValueIndex(List<Double> data, int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (data.get(i) != null) {
                return i;
            }
        }
        return -1;
    }

    private double nocb(double x0, double x1) {
        return (x0 + x1) / 2.0;
    }

}

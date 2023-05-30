package com.ufcity.handler.procedures;

import java.util.*;

public class AggregateDataHandling {
    private static AggregateDataHandling instance = null;
    private char method;

    private AggregateDataHandling(){}

    public static AggregateDataHandling getInstance(){
        if(instance == null){
            instance = new AggregateDataHandling();
        }
        return instance;
    }

    public double getAggregateDataHandled(List<Double> data){
        if(this.method == METHODS.methodMap.get("MEAN_AGGREGATION_METHOD")){
            return meanAggregation(data);
        } else if (this.method == METHODS.methodMap.get("MEDIAN_AGGREGATION_METHOD")) {
            return medianAggregation(data);
        } else if (this.method == METHODS.methodMap.get("MAX_AGGREGATION_METHOD")) {
            return maxAggregation(data);
        } else if (this.method == METHODS.methodMap.get("MIN_AGGREGATION_METHOD")) {
            return minAggregation(data);
        }
        return 0;
    }

    public String getAggregateDataHandledStr(List<String> data){
        return mostFrequency(data);
    }

    public String mostFrequency(List<String> values) {
        // Cria um mapa para armazenar a frequência de cada string
        Map<String, Integer> freqMap = new HashMap<>();

        // Conta a frequência de cada string
        for (String value : values) {
            freqMap.put(value, freqMap.getOrDefault(value, 0) + 1);
        }

        // Encontra a string com a maior frequência
        int maxFreq = 0;
        String mostFreqValue = "";
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                mostFreqValue = entry.getKey();
            }
        }

        return mostFreqValue;
    }

    public char getMethod() {
        return method;
    }

    public void setMethod(char method) {
        this.method = method;
    }

    private double meanAggregation(List<Double> data) {
        double sum = 0;
        for (Double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    private double medianAggregation(List<Double> data) {
        Collections.sort(data);
        int size = data.size();
        if (size % 2 == 0) {
            int midIndex = size / 2;
            return (data.get(midIndex - 1) + data.get(midIndex)) / 2.0;
        } else {
            int midIndex = size / 2;
            return data.get(midIndex);
        }
    }

    private static double maxAggregation(List<Double> data) {
        return Collections.max(data);
    }

    private static double minAggregation(List<Double> data) {
        return Collections.min(data);
    }

}

package com.ufcity.handler.procedures;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OutliersHandling {
    private static OutliersHandling instance = null;
    private double threshold;
    private char method;
    private double upperPercentile;
    private double lowerPercentile;

    private OutliersHandling(){}

    public static OutliersHandling getInstance(){
        if(instance == null){
            instance = new OutliersHandling();
        }
        return instance;
    }

    public List<Double> getOutliersHandled(List<Double> data){
        if(this.method == METHODS.methodMap.get("IQR_REMOVE_OUTLIERS_METHOD")){
            return iqrRemoveOutliers(data);
        } else if (this.method == METHODS.methodMap.get("PERCENTILE_REMOVE_OUTLIERS_METHOD")) {
            return percentileRemoveOutliers(data);
        } else if (this.method == METHODS.methodMap.get("TUKEY_REMOVE_OUTLIERS_METHOD")) {
            return tukeyOutlierRemoval(data);
        } else if (this.method == METHODS.methodMap.get("Z_SCORE_REMOVE_OUTLIERS_METHOD")) {
            return zScoreRemoveOutliers(data);
        }
        return new ArrayList<>();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public char getMethod() {
        return method;
    }

    public void setMethod(char method) {
        this.method = method;
    }

    public double getUpperPercentile() {
        return upperPercentile;
    }

    public void setUpperPercentile(double upperPercentile) {
        this.upperPercentile = upperPercentile;
    }

    public double getLowerPercentile() {
        return lowerPercentile;
    }

    public void setLowerPercentile(double lowerPercentile) {
        this.lowerPercentile = lowerPercentile;
    }

    private List<Double> iqrRemoveOutliers(List<Double> data) {
        List<Double> outliersRemoved = new ArrayList<>(data);
        double q1 = getPercentile(data, 25);
        double q3 = getPercentile(data, 75);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        outliersRemoved.removeIf(value -> value < lowerBound || value > upperBound);
        return outliersRemoved;
    }

    private double getPercentile(List<Double> data, double percentile) {
        Collections.sort(data);
        int index = (int) Math.ceil((percentile / 100) * data.size()) - 1;
        return data.get(index);
    }

    private List<Double> percentileRemoveOutliers(List<Double> data) {
        List<Double> outliersRemoved = new ArrayList<>(data);
        double lowerBound = getPercentile(data, this.lowerPercentile);
        double upperBound = getPercentile(data, this.upperPercentile);

        outliersRemoved.removeIf(value -> value < lowerBound || value > upperBound);
        return outliersRemoved;
    }

    private List<Double> zScoreRemoveOutliers(List<Double> data) {
        List<Double> outliersRemoved = new ArrayList<>(data);
        double mean = calculateMean(data);
        double standardDeviation = calculateStandardDeviation(data, mean);
            double threshold = this.threshold; // Threshold value for determining outliers

        outliersRemoved.removeIf(value -> Math.abs((value - mean) / standardDeviation) > threshold);
        return outliersRemoved;
    }

    private double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    private double calculateStandardDeviation(List<Double> data, double mean) {
        double sumSquaredDeviations = 0.0;
        for (double value : data) {
            double deviation = value - mean;
            sumSquaredDeviations += deviation * deviation;
        }
        double variance = sumSquaredDeviations / data.size();
        return Math.sqrt(variance);
    }

    private List<Double> tukeyOutlierRemoval(List<Double> data) {
        // Converte a lista de dados para um array
        double[] dataArray = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            dataArray[i] = data.get(i);
        }

        // Calcula o primeiro quartil (Q1) e o terceiro quartil (Q3)
        double q1 = calculatePercentile(dataArray, 25);
        double q3 = calculatePercentile(dataArray, 75);

        // Calcula a amplitude interquartil (IQR)
        double iqr = q3 - q1;

        // Define os limites inferior e superior para identificar outliers
        double lowerBound = q1 - this.threshold * iqr;
        double upperBound = q3 + this.threshold * iqr;

        // Remove os outliers do conjunto de dados
        List<Double> cleanedDataList = new ArrayList<>();
        for (double value : dataArray) {
            if (value >= lowerBound && value <= upperBound) {
                cleanedDataList.add(value);
            }
        }

        return cleanedDataList;
    }

    private double calculatePercentile(double[] data, double percentile) {
        // Ordena o conjunto de dados em ordem crescente
        Arrays.sort(data);

        // Calcula a posição do percentil no conjunto de dados
        double position = (percentile / 100) * (data.length + 1);

        // Verifica se a posição é um número inteiro
        if (position % 1 == 0) {
            // Retorna o valor exato na posição do percentil
            return data[(int) position - 1];
        } else {
            // Calcula a média dos valores nas posições adjacentes do percentil
            int lowerIndex = (int) Math.floor(position) - 1;
            int upperIndex = (int) Math.ceil(position) - 1;
            return (data[lowerIndex] + data[upperIndex]) / 2;
        }
    }


}

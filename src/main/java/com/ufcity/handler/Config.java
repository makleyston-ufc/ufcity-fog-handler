package com.ufcity.handler;

import com.ufcity.handler.procedures.AggregateDataHandling;
import com.ufcity.handler.procedures.DataGroupingHandling;
import com.ufcity.handler.procedures.MissingDataHandling;
import com.ufcity.handler.procedures.OutliersHandling;
import com.ufcity.handler.storage.MongoDB;
import ufcitycore.config.ConfigInterface;

import static ufcitycore.mqtt.ConnectionData.*;

public class Config implements ConfigInterface {
    @Override
    public void configDataBase(String host, String port, String username, String password) {
        System.out.println(host + " # " + port + " # " + username + " # " + password);
        Main.database = new MongoDB(host, port);
        System.out.println(">> Connecting database! Database address: "+host+":"+port);
    }

    @Override
    public void configFogMqttBroker(String host, String port) {
        setFogHost(host);
        setFogPort(port);
    }

    @Override
    public void configCloudMqttBroker(String host, String port) {
        setCloudHost(host);
        setCloudPort(port);
    }

    @Override
    public void configSemantic(String host, String port, String username, String password) {
        //nothing to do
    }

    @Override
    public void configGroupData(char method, long time, long size) {
        DataGroupingHandling.getInstance().setMethod(method);
        DataGroupingHandling.getInstance().setTime(time);
        DataGroupingHandling.getInstance().setSize(size);
    }

    @Override
    public void configAggregateData(char method) {
        AggregateDataHandling.getInstance().setMethod(method);
    }

    @Override
    public void configMissingData(char method) {
        MissingDataHandling.getInstance().setMethod(method);
    }

    @Override
    public void configRemoveOutlier(char method, double threshold, double lowerPercentile, double upperPercentile) {
        OutliersHandling.getInstance().setMethod(method);
        OutliersHandling.getInstance().setThreshold(threshold);
        OutliersHandling.getInstance().setLowerPercentile(lowerPercentile);
        OutliersHandling.getInstance().setUpperPercentile(upperPercentile);
    }
}

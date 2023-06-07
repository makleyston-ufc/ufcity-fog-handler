package com.ufcity.handler;

import com.ufcity.handler.procedures.*;
import com.ufcity.handler.storage.MongoDB;
import ufcitycore.config.ConfigInterface;

import static ufcitycore.mqtt.ConnectionData.*;

public class Config implements ConfigInterface {
    @Override
    public void configDataBase(String host, String port, String username, String password) {
        Main.database = new MongoDB(host, port, username, password);
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
    public void configGroupData(String method, long time, long size) {
        char m = METHODS.methodMap.get("NONE");
        if(method != null)
            m = METHODS.methodMap.get(method);
        DataGroupingHandling.getInstance().setMethod(m);
        DataGroupingHandling.getInstance().setTime(time);
        DataGroupingHandling.getInstance().setSize(size);
    }

    @Override
    public void configAggregateData(String method) {
        char m = METHODS.methodMap.get("NONE");
        if(method != null)
            m = METHODS.methodMap.get(method);
        AggregateDataHandling.getInstance().setMethod(m);
    }

    @Override
    public void configMissingData(String method) {
        char m = METHODS.methodMap.get("NONE");
        if(method != null)
            m = METHODS.methodMap.get(method);
        MissingDataHandling.getInstance().setMethod(m);
    }

    @Override
    public void configRemoveOutlier(String method, double threshold, double lowerPercentile, double upperPercentile) {
        char m = METHODS.methodMap.get("NONE");
        if(method != null)
            m = METHODS.methodMap.get(method);
        OutliersHandling.getInstance().setMethod(m);
        OutliersHandling.getInstance().setThreshold(threshold);
        OutliersHandling.getInstance().setLowerPercentile(lowerPercentile);
        OutliersHandling.getInstance().setUpperPercentile(upperPercentile);
    }
}

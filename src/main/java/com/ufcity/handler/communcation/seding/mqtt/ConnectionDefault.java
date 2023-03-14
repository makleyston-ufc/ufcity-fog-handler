package com.ufcity.handler.communcation.seding.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

public abstract class ConnectionDefault {

    protected MqttClient client;

    protected ConnectionConfig connectionConfig;

    ConnectionDefault(){
        this.connectionConfig = new ConnectionConfig();
    }

    ConnectionDefault(ConnectionConfig connectionConfig){
        this.connectionConfig = connectionConfig;
    }

    public void destroy() {
        try {
            if (client != null) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}

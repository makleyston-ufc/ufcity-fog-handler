package com.ufcity.handler.communcation.seding.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

public class Subscribe extends ConnectionDefault {


    public Subscribe(ConnectionConfig connectionConfig) {
        super(connectionConfig);
    }

    public void subscribe(MessageObserver messageObserver) throws MqttException {
        String clientId = "handler_sub:"+now();
        try {
            client = new MqttClient(connectionConfig.getServerURI(), clientId);
            client.connect();
            client.subscribe(connectionConfig.getTopics());
            client.setCallback(new MQTTCallBack(messageObserver));
        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
            client.disconnect();
        }
    }

}

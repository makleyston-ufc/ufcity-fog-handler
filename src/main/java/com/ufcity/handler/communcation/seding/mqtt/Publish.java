package com.ufcity.handler.communcation.seding.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;

public class Publish extends ConnectionDefault{

    public Publish(ConnectionConfig connectionConfig){
        super(connectionConfig);
    }

    public void publish(String message) throws MqttException {
        String clientId = "handler_pub:"+now();

        client = new MqttClient(this.connectionConfig.getServerURI(), clientId);
        client.connect();
        client.publish(
                connectionConfig.getTopics()[0], //It uses the first element of array.
                message.getBytes(UTF_8),
                0, // QoS = 0
                false);

        client.disconnect();
    }
}

package com.ufcity.handler.communcation.sending.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.PREFIX;
import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.PUB;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;

public class Publish extends ConnectionDefault{

    public Publish(ConnectionConfig connectionConfig){
        super(connectionConfig);
    }

    public void publish(String message) throws MqttException {
        String clientId = PREFIX+PUB+now();
        client = new MqttClient(this.connectionConfig.getServerURI(), clientId);
        client.connect();
        for (String topic : connectionConfig.getTopics()) {
            client.publish(topic, message.getBytes(UTF_8),0, false);
        }
        client.disconnect();
    }
}

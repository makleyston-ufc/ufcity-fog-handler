package com.ufcity.handler.communcation.sending.mqtt;

import com.ufcity.handler.Main;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Arrays;
import java.util.logging.Logger;

import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.PREFIX;
import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.SUB;
import static java.time.LocalDateTime.now;

public class Subscribe extends ConnectionDefault {
    static Logger log = Logger.getLogger(Main.class.getName());
//    ConnectionConfig conn;
    String clientId = PREFIX+SUB+now();

    public Subscribe(ConnectionConfig connectionConfig) {
        super(connectionConfig);
//        this.conn = connectionConfig;
    }

    public void subscribe(MessageObserver messageObserver) throws MqttException {
        try {
            System.out.println("SUB# ServerURI: " + connectionConfig.getServerURI());
            client = new MqttClient(connectionConfig.getServerURI(), clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);
            client.setCallback(new MQTTCallBack(messageObserver));
//            int[] qos = new int[connectionConfig.getTopics().length];
//            for (int i = 0; i < connectionConfig.getTopics().length; i++) {
//                qos[i] = 0;
//            }
            client.subscribe(connectionConfig.getTopics());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setClientId(String clientId){
        this.clientId = PREFIX+SUB+clientId+now();
    }

}

package com.ufcity.handler;

import com.ufcity.handler.communcation.seding.mqtt.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws MqttException {
        log.info("The Handler Service is starting its settings.\n" +
                "Please wait a few moments.\n\n");

        /**
         * Initializing the MQTT Broker for edge communication
         */
        ConnectionConfig connectionConfigSubEdge = new ConnectionConfig(ConnectionData.HOST_EDGE, ConnectionData.PORT_EDGE);
        Subscribe subscribeEdge = new Subscribe(connectionConfigSubEdge);
        subscribeEdge.subscribe(new MessageObserver() {
            @Override
            public void receiveMessage(String topic, String message) {
                log.info("Topic: "+topic+", Message: "+message);

                //TODO fazer um cache sobre se o recurso já foi enviado ao componente de combinção de recursos
                // para eliminar a quantidade de dados publicados na rede.

                /**
                 * Sending for Services Combination
                 */
                log.info("Sending services info to Services Combination component!\n");
                ConnectionConfig connectionConfigInner = new ConnectionConfig(ConnectionData.HOST_INNER, ConnectionData.PORT_INNER);
                connectionConfigInner.setTopics(new ArrayList<>(Arrays.asList("/test")));
                Publish publish = new Publish(connectionConfigInner);
                try {
                    publish.publish("Oi");
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }

                Object obj = new Object();
                try {
                    steps(obj);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /**
         * Initializing the MQTT Broker for inner communication
         */
        ConnectionConfig connectionConfigSubInner = new ConnectionConfig(ConnectionData.HOST_INNER, ConnectionData.PORT_INNER);
        Subscribe subscribeInner = new Subscribe(connectionConfigSubInner);
        subscribeInner.subscribe(new MessageObserver() {
            @Override
            public void receiveMessage(String topic, String message) {
                log.info("Topic: "+topic+", Message: "+message);
                Object obj = new Object();
                //TODO
                try {
                    steps(obj);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public static void steps(Object obj) throws MqttException {

        /**
         * Removing outliers (default)
         */



        /**
         * Reduce the data by grouping (default)
         */


        /**
         * Start all procedures on data
         */

        /**
         * Saving in cache the important data and analysis result of resources
         */
        log.info("Saving in cache the important data and analysis result of resources\n");
        SaveResourceData saveResourceData = SaveResourceData.getInstance();
        saveResourceData.addResource((String) obj);

        /**
         * Sending data to Tracking or Cloud
         */
        log.info("Sending data to Tracking component!\n");
        ConnectionConfig connectionConfigInner = new ConnectionConfig(ConnectionData.HOST_INNER, ConnectionData.PORT_INNER);

        List<String> topicTrackingList = new ArrayList<>(Arrays.asList("/test"));
        List<String> topicCloudList = new ArrayList<>(Arrays.asList("/test"));

        //TODO verify if is Tracking or Cloud
        connectionConfigInner.setTopics(topicTrackingList);

        Publish publish = new Publish(connectionConfigInner);
        try {
            publish.publish("Oi");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }


    }

    public static class SaveResourceData{
        private List<String> resources = new ArrayList<>();
        private static SaveResourceData instance = null;

        private SaveResourceData(){}

        public static SaveResourceData getInstance(){
            if(instance == null){
                instance = new SaveResourceData();
            }
            return instance;
        }

        public void addResource(String resourceSemantic){
            this.resources.add(resourceSemantic)
;       }

        public void removeResource(String resourceSemantic){
            this.resources.remove(resourceSemantic);
        }

        public List<String> getResources() {
            return resources;
        }

        public void setResources(List<String> resources) {
            this.resources = resources;
        }
    }
}
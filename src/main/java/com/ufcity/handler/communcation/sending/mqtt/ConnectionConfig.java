package com.ufcity.handler.communcation.sending.mqtt;

import java.util.ArrayList;
import java.util.List;

public class ConnectionConfig {

    private String host;
    private String port;
    private List<String> topics = new ArrayList<>();

    public ConnectionConfig(){
        this.port = ConnectionData.EDGE_PORT; //port default
        this.host = ConnectionData.EDGE_HOST; //host default
    }

    public ConnectionConfig(String host, String port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    String getServerURI(){
        return "tcp://"+getHost()+":"+getPort();
    }

    public String[] getTopics() {
        return (String[]) this.topics.toArray(new String[0]);
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void setTopic(String topic) {
        this.topics.clear();
        this.topics.add(topic);
    }

    public void addTopic(String topic){
        this.topics.add(topic);
    }

    public void clearTopics(){
        this.topics.clear();
    }


}

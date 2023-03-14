package com.ufcity.handler.communcation.seding.mqtt;

import java.util.ArrayList;
import java.util.List;

public class ConnectionConfig {

    private String host;
    private String port;
    private List<String> topics = new ArrayList<>();

    public ConnectionConfig(){
        this.port = ConnectionData.PORT_EDGE; //port default
        this.host = ConnectionData.HOST_EDGE; //host default
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

    public void addTopic(String topic){
        this.topics.add(topic);
    }


}

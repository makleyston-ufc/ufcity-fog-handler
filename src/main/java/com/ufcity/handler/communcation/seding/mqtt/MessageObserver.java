package com.ufcity.handler.communcation.seding.mqtt;

public interface MessageObserver {

    void receiveMessage(String topic, String message);

}

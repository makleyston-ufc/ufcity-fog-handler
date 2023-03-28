package com.ufcity.handler.communcation.sending.mqtt;

public interface MessageObserver {

    void receiveMessage(String topic, String message);

}

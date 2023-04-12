package com.ufcity.handler.storage;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;
import org.bson.Document;

public class MongoDB implements Database{

    final String HOST = "localhost";
//    MongoClient mongoClient = MongoClients.create("mongodb://"+HOST+":27017");
//    MongoDatabase database = mongoClient.getDatabase("ufcity");

    @Override
    public void saveDevice(Device device) {
//        MongoCollection<Document> doc = database.getCollection("device");
        //TODO
    }

    @Override
    public void removeDevice(Device device) {
//TODO
    }

    @Override
    public void saveResource(String uuidDevice, Resource resource) {
//TODO
    }

    @Override
    public void removeResource(String uuidDevice, Resource resource) {
//TODO
    }

    @Override
    public void removeResourceByUUID(String uuidDevice, String uuid_resource) {
//TODO
    }

    @Override
    public void updateDevice(Device device) {
//TODO
    }

    @Override
    public void updateResource(String uuidDevice, Resource resource) {
//TODO
    }
}

package com.ufcity.handler.semantic;

import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;

public class Jena extends Semantic{

    public Jena(String host, String port) {
        super(host, port);
    }

    @Override
    public void createSemantic(Device device) {
        //TODO
    }

    @Override
    public void createSemantic(Resource resource) {
//TODO
    }

    @Override
    public void saveDevice(Device device) {
//TODO
    }

    @Override
    public void saveResource(String uuidDevice, Resource resource) {
//TODO
    }

    @Override
    public void removeDevice(Device device) {
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

    }

    @Override
    public void updateResource(String uuidDevice, Resource resource) {

    }
}

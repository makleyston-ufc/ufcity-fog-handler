package com.ufcity.handler.storage;

import ufcitycore.models.Device;
import ufcitycore.models.Resource;

public abstract class Database {


    private String host;
    private String port;

    public Database(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public abstract void saveDevice(Device device);
    public abstract void removeDevice(Device device);
    public abstract void saveResource(String uuidDevice, Resource resource);
    public abstract void removeResource(String uuidDevice, Resource resource);
    public abstract void removeResourceByUUID(String uuidDevice, String uuid_resource);
    public abstract void updateDevice(Device device);
    public abstract void updateResource(String uuidDevice, Resource resource);
    public abstract String setOrGetFogUUIDFog(String uuid_fog);


}

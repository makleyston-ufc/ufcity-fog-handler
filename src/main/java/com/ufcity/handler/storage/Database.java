package com.ufcity.handler.storage;

import ufcitycore.models.Device;
import ufcitycore.models.Resource;

public abstract class Database {


    private String host;
    private String port;
    private String username;
    private String password;

    public Database(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
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

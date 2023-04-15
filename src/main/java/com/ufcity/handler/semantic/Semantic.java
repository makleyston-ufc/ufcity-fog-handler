package com.ufcity.handler.semantic;

import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;

import java.util.HashMap;
import java.util.Map;

public abstract class Semantic {
    private String host;
    private String port;

    public Semantic(String host, String port) {
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

    public abstract void createSemantic(Device device);
    public abstract void createSemantic(Resource resource);
    public abstract void saveDevice(Device device);
    public abstract void saveResource(String uuidDevice, Resource resource);
    public abstract void removeDevice(Device device);
    public abstract void removeResource(String uuidDevice, Resource resource);
    public abstract void removeResourceByUUID(String uuidDevice, String uuid_resource);
    public abstract void updateDevice(Device device);
    public abstract void updateResource(String uuidDevice, Resource resource);

}

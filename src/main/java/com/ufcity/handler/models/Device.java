package com.ufcity.handler.models;

import java.util.ArrayList;
import java.util.List;

public class Device {
    private String device_uuid;
    Location location;
    private List<Resource> resources = new ArrayList<>();

    public String getDevice_uuid() {
        return device_uuid;
    }

    public Location getLocation() {
        return location;
    }

    public void setDevice_uuid(String device_uuid) {
        this.device_uuid = device_uuid;
    }

    public void setLocation(Location locationObject) {
        this.location = locationObject;
    }

    public void addResource(Resource resource){
        this.resources.add(resource);
    }

    public void removeResource(Resource resource){
        for (Resource r :
                this.resources) {
            if (r.getResource_uuid().equals(resource.getResource_uuid())){
                this.resources.remove(r);
            }
        }
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}

package com.ufcity.handler.models;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Resource {

    private Location location;
    private String resource_uuid;
    ArrayList < Service > services = new ArrayList< Service >();

    public ArrayList< Service > getServices(){
        return this.services;
    }

    public Location getLocation() {
        return location;
    }

    public String getResource_uuid() {
        return resource_uuid;
    }

    public void setLocation(Location locationObject) {
        this.location = locationObject;
    }

    public void setResource_uuid(String resource_uuid) {
        this.resource_uuid = resource_uuid;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

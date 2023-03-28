package com.ufcity.handler.models;

import java.util.ArrayList;
import java.util.Map;

public class Service {
    private String service_uuid;
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

    public String getService_uuid() {
        return service_uuid;
    }

    public ArrayList<Map<String, String>> getData() {
        return data;
    }

    public void setService_uuid(String service_uuid) {
        this.service_uuid = service_uuid;
    }




}

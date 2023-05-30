package com.ufcity.handler.model;

import ufcitycore.models.Resource;

import java.time.LocalDateTime;

public class Data {

    Resource resource;
    LocalDateTime localDateTime;

    public Data(Resource resource, LocalDateTime localDateTime) {
        this.resource = resource;
        this.localDateTime = localDateTime;
    }

    public Resource getResource() {
        return resource;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}

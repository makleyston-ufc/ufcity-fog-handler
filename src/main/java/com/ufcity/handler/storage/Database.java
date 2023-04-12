package com.ufcity.handler.storage;

import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;

public interface Database {

    public void saveDevice(Device device);
    public void removeDevice(Device device);
    public void saveResource(String uuidDevice, Resource resource);
    public void removeResource(String uuidDevice, Resource resource);
    public void removeResourceByUUID(String uuidDevice, String uuid_resource);
    public void updateDevice(Device device);
    public void updateResource(String uuidDevice, Resource resource);


}

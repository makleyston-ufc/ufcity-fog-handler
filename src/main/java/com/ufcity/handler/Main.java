package com.ufcity.handler;

import com.google.gson.Gson;

import com.ufcity.handler.model.Data;
import com.ufcity.handler.procedures.AggregateDataHandling;
import com.ufcity.handler.procedures.DataGroupingHandling;
import com.ufcity.handler.procedures.MissingDataHandling;
import com.ufcity.handler.procedures.OutliersHandling;
import com.ufcity.handler.storage.Database;
import org.eclipse.paho.client.mqttv3.*;
import ufcitycore.models.Device;
import ufcitycore.models.Resource;
import ufcitycore.models.Service;
import ufcitycore.mqtt.ConnectionConfig;
import ufcitycore.mqtt.Publish;
import ufcitycore.mqtt.Subscribe;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

import static ufcitycore.config.Config.*;
import static ufcitycore.mqtt.ConnectionData.*;

public class Main {
    static Logger log = Logger.getLogger(Main.class.getName());
    static String uuidItself = UUID.randomUUID().toString();
    static Gson gson = new Gson();
    static Database database;
    static final String version = "0.1";

    static ConnectionConfig connectionConfigCloud = null;

    public static void main(String[] args) throws MqttException {

        try {
            ReaderYAMLConfig(new Config());
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found or not properly written!");
            throw new RuntimeException(e);
        }

        log.info("The Handler Service is starting its settings.\n" +
                "Please wait a few moments.");

        uuidItself = database.setOrGetFogUUIDFog(uuidItself);

        if(!SaveInMemory.getInstance().findDeviceByUUID(uuidItself)) {
            Device device = new Device();
            device.setUuid_device(uuidItself);
            SaveInMemory.getInstance().addDevice(device);
        }

        /*  Initializing the MQTT Broker client - Cloud Computing */
        connectionConfigCloud = new ConnectionConfig(CLOUD_HOST, CLOUD_PORT);

        /*  Initializing the MQTT Broker client - Fog Computing */
        System.out.println("### MQTT Broker ###");
        ConnectionConfig connectionConfigSubEdge = new ConnectionConfig(INNER_HOST, INNER_PORT);
        connectionConfigSubEdge.setTopics(getEdgeSubscribeTopics());
        Subscribe subscribeEdge = new Subscribe(connectionConfigSubEdge);
        subscribeEdge.subscribe((topic, message) -> {
            System.out.println("## Topic: "+topic+", Message: "+message);
            String[] topicSep = topic.split("/");
            String firstLevelTopic = topicSep[0];
            switch (firstLevelTopic) {
                case EDGE_DEVICE_SUBSCRIBE ->
                    /* device/[uuid_device] -> json */
                        newDevice(topicSep[1], message);
                case EDGE_REGISTERED_RESOURCES_SUBSCRIBE ->
                    /* Topic: registered_resource/[uuid_device] -> json */
                        registeredResource(topicSep[1], message);
                case EDGE_REMOVED_RESOURCES_SUBSCRIBE ->
                    /* Topic: removed_resource/[uuid_device] */
                        removedResource(topicSep[1], message);
                case EDGE_RESOURCES_DATA_SUBSCRIBE ->
                    /* Topic: resource_data/[uuid_device]/[uuid_resource] */
                        receivedResourceData(topicSep[1], message);
            }
        });
    }

    private static void receivedResourceData(String uuid_device, String message) {
        System.out.println(">> Resource data.");
        Resource resource = gson.fromJson(message, Resource.class);
        SaveInMemory sm = SaveInMemory.getInstance();
        Device d = sm.getDeviceByUUID(uuid_device);

        if(d == null){
            System.err.println("Device don't registered!");
            ConnectionConfig connectionConfigPubEdge = new ConnectionConfig(INNER_HOST, INNER_PORT);
            connectionConfigPubEdge.setTopic(getResendDeviceTopic(uuid_device));
            new Publish(connectionConfigPubEdge).publish(uuid_device);
            return;
        }

        if (d.getResourceByUUID(resource.getUuid_resource()) == null){
            System.err.println("Resource don't registered!");
            registeredResource(uuid_device, message);
            return;
        }

        /* Adding resource data to queue */
        DataGroupingHandling.getInstance().addData(resource);
        /* Verify conditions to publish (Data Grouping) */
        List<List<Data>> queueToPublish = DataGroupingHandling.getInstance().getQueuesToPublish();

        if(queueToPublish.size() > 0){

            /* List of the resources to publish */
            List<Resource> result = new ArrayList<>();

            for(List<Data> oneQueue : queueToPublish) {
                Resource resourceModel = oneQueue.get(0).getResource();
                Resource newResource = new Resource();
                newResource.setUuid_resource(resourceModel.getUuid_resource());
                newResource.setLocation(resourceModel.getLocation());

                for (Service serviceModel : resourceModel.getServices()) {
                    Service newService = new Service();
                    newService.setUuid_service(serviceModel.getUuid_service());

                    for (ufcitycore.models.Data dataModel : serviceModel.getData()) {
                        /* Get only values of each queue */
                        List<Double> values_dbl = new ArrayList<>();
                        List<String> values_str = new ArrayList<>();

                        for (Data dataQueue : oneQueue) {
                            Resource _resource = dataQueue.getResource();
                            String _value = _resource.getServiceByUUID(serviceModel.getUuid_service()).getDataByTag(dataModel.getTag()).getValue();
                            try {
                                double _v_double = Double.parseDouble(_value);
                                values_dbl.add(_v_double);
                            } catch (Exception e) {
                                values_str.add(_value);
                            }
                        }

                        String _result_value = "";
                        if (!values_dbl.isEmpty()) {
                            //missing data
                            List<Double> missingDataHandled = MissingDataHandling.getInstance().getMissingDataHandled(values_dbl);

                            //remove outliers
                            List<Double> outliersHandled = OutliersHandling.getInstance().getOutliersHandled(missingDataHandled);

                            //data aggregation
                            _result_value = String.valueOf(AggregateDataHandling.getInstance().getAggregateDataHandled(outliersHandled));
                        } else if (!values_str.isEmpty()) {
                            /* Caso o value seja uma string, então a o value mais frequente será escolhido */
                            //data aggregation
                            _result_value = AggregateDataHandling.getInstance().getAggregateDataHandledStr(values_str);
                        }

                        ufcitycore.models.Data newServiceData = new ufcitycore.models.Data();
                        newServiceData.setTag(dataModel.getTag());
                        newServiceData.setValue(_result_value);

                        newService.addServiceData(newServiceData);
                    }

                    newResource.addService(newService);
                }

                result.add(newResource);
            }

           storageAndPublishCloud(uuid_device, result);
        }
    }

    private static void removedResource(String uuid_device, String uuid_resource) {
        System.out.println(">> Removing in memory the resource.");
        Device d = SaveInMemory.getInstance().getDeviceByUUID(uuid_device);
        if (d == null){
            System.err.println("Device don't registered!");
            return;
        }
        d.removeResourceByUUID(uuid_resource);
        /* Storage in MongoDB */
        database.removeResourceByUUID(uuid_device, uuid_resource);
    }

    private static void registeredResource(String uuid_device, String message) {
        System.out.println(">> Saving in memory the resource.");
        Resource resource = gson.fromJson(message, Resource.class);
        Device d = SaveInMemory.getInstance().getDeviceByUUID(uuid_device);
        if(d == null){
            System.err.println("Device don't registered!");
            ConnectionConfig connectionConfigPubEdge = new ConnectionConfig(INNER_HOST, INNER_PORT);
            connectionConfigPubEdge.setTopic(getResendDeviceTopic(uuid_device));
            new Publish(connectionConfigPubEdge).publish(uuid_device);
            return;
        }
        d.addResource(resource);
        /* Storage in MongoDB */
        database.saveResource(uuid_device, resource);
    }

    private static void newDevice(String uuid_device, String message) {
        if(SaveInMemory.getInstance().getDeviceByUUID(uuid_device) != null) {
            System.out.println("Device already registered!");
            return;
        }
        System.out.println(">> Saving device data in memory.");
        Device device = gson.fromJson(message, Device.class);
        SaveInMemory.getInstance().addDevice(device);
        /* Storage in MongoDB */
        database.saveDevice(device);
    }

    public static void storageAndPublishCloud(String uuid_device, List<Resource> resources) {
        System.out.println(">> Sending resource data to Cloud Computing.");
        for (Resource resource : resources) {
            /* Update resource on MongoDB */
            database.updateResource(uuid_device, resource);
            /* Publishing resource on Cloud */
            connectionConfigCloud.setTopic("cloud/"+getCloudResourceDataTopic(uuidItself, uuid_device, resource.getUuid_resource()));
            Publish publish = new Publish(connectionConfigCloud);
            publish.publish(resource.toJson());
        }
    }

    public static class SaveInMemory {
        private final List<Device> devices = new ArrayList<>();
        private static SaveInMemory instance = null;
        private List<Resource> resourceData = new ArrayList<>();

        private SaveInMemory(){}

        public static SaveInMemory getInstance(){
            if(instance == null){
                instance = new SaveInMemory();
            }
            return instance;
        }

        public List<Device> getDevices() {
            return devices;
        }

        public void addDevice(Device device) {
            this.devices.add(device);
        }

        public void addResource(Device device, Resource resource){
            if(device.getResourceByUUID(resource.getUuid_resource()) != null){
                System.out.println("Resource already registered!");
                return;
            }
            device.addResource(resource);
        }

        public void updateResource(Device device, Resource resource){
            boolean flag = false;
            for(int i = 0; i < device.getResources().size(); i++){
                if(device.getResources().get(i).getUuid_resource().equals(resource.getUuid_resource())){
                    device.getResources().set(i, resource);
                    flag = true;
                }
            }
            if(!flag) System.out.println("Resource don't registered!");
        }

        public void addResourceData(Device device, Resource resource){

        }

        public void removeDevice(Device device){
            this.devices.removeIf(d -> d.getUuid_device().equals(device.getUuid_device()));
        }

        public Device getDeviceByUUID(String uuid){
            for (Device d : this.devices) {
                if (d.getUuid_device().equals(uuid)) {
                    return d;
                }
            }
            return null;
        }

        public boolean findDeviceByUUID(String uuid){
            return this.devices.stream().anyMatch(d -> d.getUuid_device().equals(uuid));
        }

    }

}
package com.ufcity.handler;

import com.google.gson.Gson;
import com.ufcity.handler.communcation.sending.mqtt.*;
import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;
import com.ufcity.handler.procedures.RemovingOutliers;
import com.ufcity.handler.semantic.Jena;
import com.ufcity.handler.semantic.Semantic;
import com.ufcity.handler.storage.Database;
import com.ufcity.handler.storage.MongoDB;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.*;
import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.getDeviceSubscribeTopic;

public class Main {

    static Logger log = Logger.getLogger(Main.class.getName());

    static String uuidItself = UUID.randomUUID().toString();

    public static void main(String[] args) throws MqttException {

        //TODO persistir o uuidDevice para mesmo após um reinício o mesmo nó da fog computing possa ter o mesmo UUID.

        if(Menu(args)!=0) return;

        log.info("The Handler Service is starting its settings.\n" +
                "Please wait a few moments.");

        Gson gson = new Gson();

        /* Initializing the MQTT Broker for inner communication */
        ConnectionConfig connectionConfigSubInner = new ConnectionConfig(HOST_INNER, PORT_INNER);
        connectionConfigSubInner.clearTopics();
        connectionConfigSubInner.addTopic(getInnerResourceDataSubscribeTopic());
        Subscribe subscribeInner = new Subscribe(connectionConfigSubInner);
        subscribeInner.subscribe((topic, message) -> {
            System.out.println("Message received in the Handler of the fog computing itself. ");
            System.out.println("Topic: "+topic+", Message: "+message);
            Resource resource = gson.fromJson(message, Resource.class);
            if(!SaveInMemory.getInstance().findDeviceByUUID(uuidItself)) {
                Device device = new Device();
                device.setDevice_uuid(uuidItself);
                SaveInMemory.getInstance().addDevice(device);
            }
            SaveInMemory.getInstance().getDeviceByUUID(uuidItself).addResource(resource);
            System.out.println("Combined service registered locally!");
            storageAndPublishCloud(uuidItself, resource);
        });

        /*  Initializing the MQTT Broker for edge communication. */
        ConnectionConfig connectionConfigSubEdge = new ConnectionConfig(HOST_EDGE, PORT_EDGE);
        connectionConfigSubEdge.addTopic(getRegisteredResourcesSubscribeTopic());
        connectionConfigSubEdge.addTopic(getResourcesDataTopic());
        connectionConfigSubEdge.addTopic(getRemovedResourcesSubscribeTopic());
        connectionConfigSubEdge.addTopic(getDeviceSubscribeTopic());
        Subscribe subscribeEdge = new Subscribe(connectionConfigSubEdge);
        subscribeEdge.subscribe((topic, message) -> {
            System.out.println("Received message in Handler from Edge Computing: ");
            System.out.println("Topic: "+topic+", Message: "+message);
            String[] topicSep = topic.split("/");
            String firstLevelTopic = topicSep[0];
            if(firstLevelTopic.equals(EDGE_DEVICE_SUBSCRIBE)){
                /*  New Device */
                /* device/[uuid_device] */
                String uuid_device = topicSep[1];
                if(!SaveInMemory.getInstance().findDeviceByUUID(uuid_device)) {
                    Device device = gson.fromJson(message, Device.class);
                    SaveInMemory.getInstance().addDevice(device);

                    /* Storage in MongoDB */
                    Database database = new MongoDB();
                    database.saveDevice(device);

                    /* Semantic annotation and save entity in FusekiJena */
                    Semantic semantic = new Jena();
                    semantic.createSemantic(device);
                    semantic.saveDevice(device);

                }else {
                    System.out.println("Device already registered!");
                }
            } else if (firstLevelTopic.equals(EDGE_REGISTERED_RESOURCES_SUBSCRIBE)) {
                /*  New resource. */
                /* Topic: registered_resource/[uuid_device] */
                String uuid_device = topicSep[1];
                Resource resource = gson.fromJson(message, Resource.class);
                try {
                    SaveInMemory.getInstance().getDeviceByUUID(uuid_device).addResource(resource);

                    /* Storage in MongoDB */
                    Database database = new MongoDB();
                    database.saveResource(uuid_device, resource);

                    /* Semantic annotation and save entity in FusekiJena */
                    Semantic semantic = new Jena();
                    semantic.createSemantic(resource);
                    semantic.saveResource(uuid_device, resource);
                }catch (Exception e){
                    System.err.println("Device don't registered!");
                    e.printStackTrace();
                }
            } else if (firstLevelTopic.equals(EDGE_REMOVED_RESOURCES_SUBSCRIBE)) {
                /* Removing the resource in cache. */
                /* Topic: removed_resource/[uuid_device] */
                String uuid_device = topicSep[1];
                Resource resource = gson.fromJson(message, Resource.class);
                try{
                    SaveInMemory.getInstance().getDeviceByUUID(uuid_device).removeResource(resource);

                    /* Storage in MongoDB */
                    Database database = new MongoDB();
                    database.removeResource(uuid_device, resource);

                    /* Semantic annotation and save entity in FusekiJena */
                    Semantic semantic = new Jena();
                    semantic.removeResource(uuid_device, resource);
                }catch (Exception e){
                    System.err.println("Device don't registered!");
                    e.printStackTrace();
                }
            } else if (firstLevelTopic.equals(EDGE_RESOURCES_DATA_SUBSCRIBE)) {
                // Topic: resource_data/[uuid_device]/[uuid_resource]
                String uuid_device = topicSep[1];
                Resource resource = gson.fromJson(message, Resource.class);

                /* Checking if is outliers */
                if(RemovingOutliers.isOutlier(SaveInMemory.getInstance().getDeviceByUUID(uuid_device).getResources(), resource)){
                    return;
                }

                /* Clustering the data of resources */
                //TODO

                /* Update semantic data */
                //TODO

                /* Update stored data */
                //TODO

                /* Sending resource to Combined Service component. */
                ConnectionConfig connectionConfigInner = new ConnectionConfig(HOST_INNER, PORT_INNER);
                connectionConfigInner.clearTopics();
                connectionConfigInner.addTopic(getInnerCombinedServicesPublishTopic(resource.getResource_uuid()));
                connectionConfigInner.addTopic(getInnerCEPResourcesDataPublishTopic(uuid_device, resource.getResource_uuid()));
                Publish publish = new Publish(connectionConfigInner);
                try {
                    publish.publish(resource.toJson());
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }

                storageAndPublishCloud(uuid_device, resource);
            }
        });

    }

    public static void storageAndPublishCloud(String uuid_device, Resource resource) {

        /* Sending data to Tracking and Cloud */
        System.out.println("Sending data to Tracking component!");
        ConnectionConfig connectionConfigInner = new ConnectionConfig(HOST_CLOUD, PORT_CLOUD);

        connectionConfigInner.clearTopics();
        connectionConfigInner.addTopic(getCloudResourceDataPublishTopic(uuidItself, uuid_device, resource.getResource_uuid()));

        Publish publish = new Publish(connectionConfigInner);
        try {
            publish.publish(resource.toJson());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        /* Update data in Fuseki Server */
        //TODO

        /* Update data in MongoDB */
        //TODO

    }

    public static class SaveInMemory {
        private List<Device> devices = new ArrayList<>();
        private static SaveInMemory instance = null;

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

        public void removeDevice(Device device){
            for (Device d :
                    this.devices) {
                if(d.getDevice_uuid().equals(device.getDevice_uuid())){
                    this.devices.remove(d);
                }
            }
        }

        public Device getDeviceByUUID(String uuid){
            for (Device d:
                    this.devices) {
                if(d.getDevice_uuid().equals(uuid))
                    return d;
            }
            return null;
        }
        public boolean findDeviceByUUID(String uuid){
            for (Device d:
                    this.devices) {
                if(d.getDevice_uuid().equals(uuid))
                    return true;
            }
            return false;
        }

    }

    public static int Menu(String[] args){
        int qtArgs = args.length;
        if(qtArgs == 0) return 1;
        if(qtArgs == 1){
            if(args[0].equals("-h") || args[0].equals("--help")){
                System.out.println("-a \t--address \tAddress to edge computing.");
                System.out.println("-A \t--ADDRESS \tAddress to cloud computing.");
                System.out.println("-p \t--port    \tPort to edge computing.");
                System.out.println("-P \t--PORT    \tPort to cloud computing.");
                System.out.println("-v \t--version \tVersion of this system.");
            } else if (args[0].equals("-v") || args[0].equals("--version")) {
                System.out.println("Version: 0.1.0 March 2023.");
            }
            return 1;
        }
        if(qtArgs % 2 != 0){
            System.out.println("Invalid parameters. Type -h (or --help) for help.");
            return 1;
        }else{
            int i = 0;
            while (i < qtArgs){
                switch (args[i]) {
                    case "-a":
                    case "-address":
                        setHostEdge(args[i+1]);
                        break;
                    case "-A":
                    case "-ADDRESS":
                        setHostCloud(args[i+1]);
                        break;
                    case "-p":
                    case "-port":
                        setPortEdge(args[i+1]);
                        break;
                    case "-P":
                    case "-PORT":
                        setPortCloud(args[i+1]);
                        break;
                }
                i = i + 2;
            }
            return 0;
        }
    }
}
package com.ufcity.handler;

import com.google.gson.Gson;
import com.ufcity.handler.communcation.sending.mqtt.*;
import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;
import com.ufcity.handler.semantic.Jena;
import com.ufcity.handler.semantic.Semantic;
import com.ufcity.handler.storage.Database;
import com.ufcity.handler.storage.MongoDB;
import org.eclipse.paho.client.mqttv3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.ufcity.handler.communcation.sending.mqtt.ConnectionData.*;

public class Main {

    static Logger log = Logger.getLogger(Main.class.getName());

    static String uuidItself = UUID.randomUUID().toString();

    static Gson gson = new Gson();

    public static void main(String[] args) throws MqttException, IOException {

        if (args.length == 0)
            if (Menu(ReaderConfig()) != 0) return;
//        else
//            if(Menu(args)!=0) return;

        log.info("The Handler Service is starting its settings.\n" +
                "Please wait a few moments.");

        /* Initializing the MQTT Broker for inner communication */

        if(!SaveInMemory.getInstance().findDeviceByUUID(uuidItself)) {
            Device device = new Device();
            device.setUuid_device(uuidItself);
            SaveInMemory.getInstance().addDevice(device);
        }

        System.out.println("### Inner Computing ###");
        ConnectionConfig connectionConfigSubInner = new ConnectionConfig(INNER_HOST, INNER_PORT);
        connectionConfigSubInner.setTopics(getInnerSubscribeTopics());
        Subscribe subscribeInner = new Subscribe(connectionConfigSubInner);
        subscribeInner.subscribe((topic, message) -> {
            System.out.println("## Message received from fog computing itself. ");
            System.out.println("## Topic: "+topic+", Message: "+message);
            Resource resource = gson.fromJson(message, Resource.class);
            //TODO Falta registrar o resource
            SaveInMemory.getInstance().getDeviceByUUID(uuidItself).addResource(resource);
//            System.out.println(">> Combined service registered locally!");

            storageAndPublishCloud(uuidItself, resource);
        });

        /*  Initializing the MQTT Broker for edge communication. */
        System.out.println("### Edge Computing ###");
        ConnectionConfig connectionConfigSubEdge = new ConnectionConfig(EDGE_HOST, EDGE_PORT);
        connectionConfigSubEdge.setTopics(getEdgeSubscribeTopics());
        Subscribe subscribeEdge = new Subscribe(connectionConfigSubEdge);
        subscribeEdge.subscribe((topic, message) -> {
            System.out.println("## Received message from Edge Computing: ");
            System.out.println("## Topic: "+topic+", Message: "+message);
            String[] topicSep = topic.split("/");
            String firstLevelTopic = topicSep[0];
            switch (firstLevelTopic) {
                case EDGE_DEVICE_SUBSCRIBE ->
                    /*  New Device */
                    /* device/[uuid_device] -> json */
                        newDevice(topicSep[1], message);
                case EDGE_REGISTERED_RESOURCES_SUBSCRIBE ->
                    /*  New resource. */
                    /* Topic: registered_resource/[uuid_device] -> json */
                        registeredResource(topicSep[1], message);
                case EDGE_REMOVED_RESOURCES_SUBSCRIBE ->
                    /* Removing the resource in cache. */
                    /* Topic: removed_resource/[uuid_device] */
                        removedResource(topicSep[1], message);
                case EDGE_RESOURCES_DATA_SUBSCRIBE ->
                    /* Topic: resource_data/[uuid_device]/[uuid_resource] */
                        receivedResourceData(topicSep[1], message);
            }
        });

    }

    private static void receivedResourceData(String uuid_device, String message) {
        System.out.println(">> Received resource data.");
        Resource resource = gson.fromJson(message, Resource.class);
        SaveInMemory sm = SaveInMemory.getInstance();
        Device d = sm.getDeviceByUUID(uuid_device);

        if(d == null){
            System.err.println("Device don't registered!");
            ConnectionConfig connectionConfigPubEdge = new ConnectionConfig(EDGE_HOST, EDGE_PORT);
            connectionConfigPubEdge.setTopic(getResendDeviceTopic(uuid_device));
            new Publish(connectionConfigPubEdge).publish(uuid_device);
            return;
        }

        if (d.getResourceByUUID(resource.getUuid_resource()) == null){
            System.err.println("Resource don't registered!");
            registeredResource(uuid_device, message);
            return;
        }

        /* Checking if is outliers */
        //TODO
//        List<Resource> resources = d.getResources();
//        if(RemovingOutliers.isOutlier(resources, resource)){
//            return;
//        }

        /* Clustering the data of resources */
        //TODO

        /* Update semantic data */
        //TODO

        /* Update stored data */
        //TODO

        /* Sending resource to Combined Service component and CEP. */
        System.out.println(">> Sending resource to Combined Service component and CEP.");
        ConnectionConfig connectionConfigInner = new ConnectionConfig(INNER_HOST, INNER_PORT);
        connectionConfigInner.clearTopics();
        connectionConfigInner.addTopic(getInnerCombinedServicesTopic(uuid_device, resource.getUuid_resource()));
        connectionConfigInner.addTopic(getInnerCEPResourcesDataPublishTopic(uuid_device, resource.getUuid_resource()));
        Publish publish = new Publish(connectionConfigInner);
        publish.publish(resource.toJson());

        storageAndPublishCloud(uuid_device, resource);
    }

    private static void removedResource(String uuid_device, String uuid_resource) {
        System.out.println(">> Removing in memory the resource.");
//        Resource resource = gson.fromJson(message, Resource.class);

        Device d = SaveInMemory.getInstance().getDeviceByUUID(uuid_device);
        if (d == null){
            System.err.println("Device don't registered!");
            return;
        }
        d.removeResourceByUUID(uuid_resource);

        /* Storage in MongoDB */
        Database database = new MongoDB();
        database.removeResourceByUUID(uuid_device, uuid_resource);

        /* Semantic annotation and save entity in FusekiJena */
        Semantic semantic = new Jena();
        semantic.removeResourceByUUID(uuid_device, uuid_resource);
    }

    private static void registeredResource(String uuid_device, String message) {

        System.out.println(">> Saving in memory the resource.");
        Resource resource = gson.fromJson(message, Resource.class);

        Device d = SaveInMemory.getInstance().getDeviceByUUID(uuid_device);
        if(d == null){
            System.err.println("Device don't registered!");
            ConnectionConfig connectionConfigPubEdge = new ConnectionConfig(EDGE_HOST, EDGE_PORT);
            connectionConfigPubEdge.setTopic(getResendDeviceTopic(uuid_device));
            new Publish(connectionConfigPubEdge).publish(uuid_device);
            return;
        }

        d.addResource(resource);

        /* Storage in MongoDB */
        Database database = new MongoDB();
        database.saveResource(uuid_device, resource);

        /* Semantic annotation and save entity in FusekiJena */
        Semantic semantic = new Jena();
        semantic.createSemantic(resource);
        semantic.saveResource(uuid_device, resource);
    }

    private static void newDevice(String uuid_device, String message) {
        if(SaveInMemory.getInstance().getDeviceByUUID(uuid_device) != null) {
            System.out.println("Device already registered!");
            return;
        }

        System.out.println(">> Saving in memory the device.");
        Device device = gson.fromJson(message, Device.class);
        SaveInMemory.getInstance().addDevice(device);

        /* Storage in MongoDB */
        Database database = new MongoDB();
        database.saveDevice(device);

        /* Semantic annotation and save entity in FusekiJena */
        Semantic semantic = new Jena();
        semantic.createSemantic(device);
        semantic.saveDevice(device);
    }

    public static void storageAndPublishCloud(String uuid_device, Resource resource) {

        /* Sending data to Tracking and Cloud */
        System.out.println(">> Sending resource data to Cloud Computing.");
        ConnectionConfig connectionConfigInner = new ConnectionConfig(HOST_CLOUD, PORT_CLOUD);
        connectionConfigInner.setTopic(getCloudResourceDataTopic(uuidItself, uuid_device, resource.getUuid_resource()));
        Publish publish = new Publish(connectionConfigInner);

        String r = resource.toJson();
        publish.publish(r);

        /* Update data in Fuseki Server */
        //TODO

        /* Update data in MongoDB */
        //TODO

    }

    public static class SaveInMemory {
        private final List<Device> devices = new ArrayList<>();
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

    public static String[] ReaderConfig() throws IOException {
        String path = new File("config/ufcity-handler.config").getAbsolutePath();
//        System.out.println(path);
        BufferedReader buffRead = new BufferedReader(new FileReader(path));
        List<String> args = new ArrayList<>();
        String line = "";
        while (true) {
            line = buffRead.readLine();
            if (line != null) {
                String[] l = line.split(" ");
                args.add(l[0]);
                args.add(l[1]);
//                System.out.println(l[0] + " ## " + l[1]);
            } else {
                buffRead.close();
                System.out.println(Arrays.toString(args.toArray(new String[0])));
                return args.toArray(new String[0]);
            }
        }
    }

    public static int Menu(String[] params){
        int qtArgs = params.length;
        if(qtArgs == 0) {
            System.out.println("Invalid parameters. Type -h (or --help) for help.");
            return 1;
        }
        if(qtArgs == 1){
            if(params[0].equals("-h") || params[0].equals("--help")){
                System.out.println("-ea \t--edge-address    \tAddress to edge computing.");
                System.out.println("-fa \t--fog-address     \tAddress to fog computing.");
                System.out.println("-ca \t--cloud-address   \tAddress to cloud computing.");
                System.out.println("-ep \t--edge-port       \tPort to edge computing.");
                System.out.println("-fp \t--fog-port        \tPort to edge computing.");
                System.out.println("-cp \t--cloud-port      \tPort to cloud computing.");
                System.out.println("-v  \t--version         \tVersion of this system.");
            } else if (params[0].equals("-v") || params[0].equals("--version")) {
                System.out.println("Version: 0.1.0 March 2023.");
            } else {
                System.out.println("Invalid parameters. Type -h (or --help) for help.");
            }
            return 1;
        }
        if(qtArgs % 2 != 0){
            System.out.println("Invalid parameters. Type -h (or --help) for help.");
            return 1;
        }else{
            int i = 0;
            while (i < qtArgs){
                switch (params[i]) {
                    case "-ea", "--edge-address" -> setEdgeHost(params[i + 1]);
                    case "-fa", "--fog-address" -> setInnerHost(params[i + 1]);
                    case "-ca", "--cloud-address" -> setHostCloud(params[i + 1]);
                    case "-ep", "--edge-port" -> setEdgePort(params[i + 1]);
                    case "-fp", "--fog-port" -> setInnerPort(params[i + 1]);
                    case "-cp", "--cloud-port" -> setPortCloud(params[i + 1]);
                }
                i = i + 2;
            }
//            setHostInner("172.23.0.4");
            return 0;
        }
    }
}
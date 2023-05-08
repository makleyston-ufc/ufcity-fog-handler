package com.ufcity.handler.communcation.sending.mqtt;

import java.util.ArrayList;
import java.util.List;

public class ConnectionData {
    final public static String PREFIX = "handler_";
    final public static String PUB = "pub_";
    final public static String SUB = "sub_";
//    public static String EDGE_PORT = "1883";
    public static String INNER_PORT = "1883";
    public static String PORT_CLOUD = "1883";
//    public static String EDGE_HOST;
    public static String INNER_HOST;
    public static String HOST_CLOUD;
    final public static String EDGE_RESOURCES_DATA_SUBSCRIBE = "resource_data";
    final public static String EDGE_DEVICE_SUBSCRIBE = "device";
    final public static String EDGE_REMOVED_RESOURCES_SUBSCRIBE = "removed_resource";
    final public static String EDGE_REGISTERED_RESOURCES_SUBSCRIBE = "registered_resource";
    final public static String EDGE_RESOURCE_COMMANDS_PUBLISH = "commands_fog_to_edge";
    final public static String EDGE_RESEND_RESOURCE_DATA_PUBLISH = "resend";
    final public static String INNER_CEP_RESOURCE_DATA_PUBLISH = "cep";
    final public static String INNER_RESOURCE_DATA = "resource_data";
    final public static String INNER_COMBINED_SERVICES_PUBLISH = "combined_services";

    public static List<String> getEdgeSubscribeTopics(){
        List<String> topics = new ArrayList<String>();
        topics.add(EDGE_DEVICE_SUBSCRIBE+"/+");
        topics.add(EDGE_RESOURCES_DATA_SUBSCRIBE+"/+/+");
        topics.add(EDGE_REMOVED_RESOURCES_SUBSCRIBE+"/+");
        topics.add(EDGE_REGISTERED_RESOURCES_SUBSCRIBE+"/+");
        return topics;
    }

    public static List<String> getInnerSubscribeTopics(){
        List<String> topics = new ArrayList<String>();
        topics.add(INNER_RESOURCE_DATA +"/+");
        return topics;
    }

    public static String getInnerPort() {
        return INNER_PORT;
    }

    public static void setInnerPort(String innerPort) {
        INNER_PORT = innerPort;
    }

    public static String getInnerHost() {
        return INNER_HOST;
    }

    public static void setInnerHost(String innerHost) {
        INNER_HOST = innerHost;
    }

//    public static String getEdgePort() {
//        return EDGE_PORT;
//    }
//
//    public static void setEdgePort(String edgePort) {
//        EDGE_PORT = edgePort;
//    }

    public static String getPortCloud() {
        return PORT_CLOUD;
    }

    public static void setPortCloud(String portCloud) {
        PORT_CLOUD = portCloud;
    }

//    public static String getEdgeHost() {
//        return EDGE_HOST;
//    }

//    public static void setEdgeHost(String edgeHost) {
//        EDGE_HOST = edgeHost;
//    }

    public static String getHostCloud() {
        return HOST_CLOUD;
    }

    public static void setHostCloud(String hostCloud) {
        HOST_CLOUD = hostCloud;
    }

    /* resource_data/uuid_device/uuid_resource     -> Message is resource data */
    public static String getInnerCEPResourcesDataPublishTopic(String uuidDevice, String uuidResource){
        return INNER_CEP_RESOURCE_DATA_PUBLISH +"/"+uuidDevice+"/"+uuidResource;
    }

    /* combined_services/uuid_resource */
    public static String getInnerCombinedServicesTopic(String uuidDevice, String uuidResource){
        return INNER_COMBINED_SERVICES_PUBLISH + "/" + uuidDevice + "/" + uuidResource;
    }

    public static String getResendDeviceTopic(String uuidDevice){
        return EDGE_RESEND_RESOURCE_DATA_PUBLISH+"/"+uuidDevice;
    }

    public static String getResendResourceTopic(String uuidDevice, String uuidResource){
        return EDGE_RESEND_RESOURCE_DATA_PUBLISH+"/"+uuidDevice+"/"+uuidResource;
    }

    /* resource_data/uuid_fog/uuid_device/uuid_resource	-> resource_json */
    public static String getCloudResourceDataTopic(String uuidItself, String uuidDevice, String uuiResource){
        return "resource_data/"+uuidItself+"/"+uuidDevice+"/"+uuiResource;
    }


}

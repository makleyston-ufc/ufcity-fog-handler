package com.ufcity.handler.communcation.sending.mqtt;

public class ConnectionData {
    final public static String PREFIX = "handler_";
    final public static String PUB = "pub_";
    final public static String SUB = "sub_";
    public static String PORT_EDGE = "1883";
    final public static String PORT_INNER = "1883";
    public static String PORT_CLOUD = "1883";
    public static String HOST_EDGE = "172.18.0.2";
    final public static String HOST_INNER = "172.19.0.2";
    public static String HOST_CLOUD = "127.0.0.1";
    final public static String EDGE_RESOURCES_DATA_SUBSCRIBE = "resource_data";
    final public static String EDGE_DEVICE_SUBSCRIBE = "device";
    final public static String EDGE_REMOVED_RESOURCES_SUBSCRIBE = "removed_resource";
    final public static String EDGE_REGISTERED_RESOURCES_SUBSCRIBE = "registered_resource";
    final public static String EDGE_RESOURCE_COMMANDS_PUBLISH = "commands_fog_to_edge";
    final public static String INNER_CEP_RESOURCE_DATA_PUBLISH = "cep";
    final public static String INNER_RESOURCE_DATA_PUBLISH = "resource_data";
    final public static String INNER_COMBINED_SERVICES_PUBLISH = "combined_services";

    public static String getPortEdge() {
        return PORT_EDGE;
    }

    public static void setPortEdge(String portEdge) {
        PORT_EDGE = portEdge;
    }

    public static String getPortCloud() {
        return PORT_CLOUD;
    }

    public static void setPortCloud(String portCloud) {
        PORT_CLOUD = portCloud;
    }

    public static String getHostEdge() {
        return HOST_EDGE;
    }

    public static void setHostEdge(String hostEdge) {
        HOST_EDGE = hostEdge;
    }

    public static String getHostCloud() {
        return HOST_CLOUD;
    }

    public static void setHostCloud(String hostCloud) {
        HOST_CLOUD = hostCloud;
    }

    public static String getDeviceSubscribeTopic(){
        return EDGE_DEVICE_SUBSCRIBE + "/+" ;
    }

    /* resource_data/uuid_device/uuid_resource     -> Message is resource data */
    public static String getRegisteredResourcesSubscribeTopic(){
        return EDGE_REGISTERED_RESOURCES_SUBSCRIBE+ "/+";
    }

    /* removed_resource/device_uuid    -> Message is uuid_resource */
    public static String getRemovedResourcesSubscribeTopic(){
        return EDGE_REMOVED_RESOURCES_SUBSCRIBE + "/+" ;
    }

    /* resource_data/uuid_device/uuid_resource     -> Message is resource data */
    public static String getResourcesDataTopic(){
        return EDGE_RESOURCES_DATA_SUBSCRIBE+ "/+/+";
    }

    /* commands_received/device_uuid/resource_uuid/    -> Message is Resource JSON */
    public static String getCommandsToResourcesPublishTopic(String deviceUUID, String resourceUUID){
        return EDGE_RESOURCE_COMMANDS_PUBLISH + "/" + deviceUUID + "/" + resourceUUID;
    }

    /* resource_data/uuid_device/uuid_resource     -> Message is resource data */
    public static String getInnerCEPResourcesDataPublishTopic(String uuidDevice, String uuidResource){
        return INNER_CEP_RESOURCE_DATA_PUBLISH +"/"+uuidDevice+"/"+uuidResource;
    }

    /* combined_services/uuid_resource */
    public static String getInnerCombinedServicesPublishTopic(String uuidResource){
        return INNER_COMBINED_SERVICES_PUBLISH + "/" + uuidResource;
    }
    public static String getInnerResourceDataSubscribeTopic(){
        return  INNER_RESOURCE_DATA_PUBLISH+"/+";
    }

    /* resource_data/uuid_fog/uuid_device/uuid_resource	-> resource_json */
    public static String getCloudResourceDataPublishTopic(String uuidItself, String uuidDevice, String uuiResource){
        return "resource_data/"+uuidItself+"/"+uuidDevice+"/"+uuiResource;
    }


}

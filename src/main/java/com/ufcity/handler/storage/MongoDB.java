package com.ufcity.handler.storage;

import com.google.gson.Gson;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.ufcity.handler.models.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Projections.excludeId;

public class MongoDB extends Database{

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collectionDevices;

    Gson gson = new Gson();


    public MongoDB(String host, String port) {
        super(host, port);
        mongoClient = MongoClients.create("mongodb://root:example@"+host+":"+port);
        database = mongoClient.getDatabase("ufcity");
        collectionDevices = database.getCollection("devices");
    }

    @Override
    public void saveDevice(Device device) {
        System.out.print(">> Saving device "+ device.getUuid_device() +" in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", device.getUuid_device());
        Document docDev = collectionDevices.find(filter).first();
        if(docDev == null){
            collectionDevices.insertOne(createDocument(device));
        }
        System.out.println(" ...OK");
    }

    @Override
    public void removeDevice(Device device) {
        System.out.print(">> Removing device "+ device.getUuid_device() +" in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", device.getUuid_device());
        collectionDevices.deleteOne(filter);
        System.out.println(" ...OK");
    }

    @Override
    public void saveResource(String uuidDevice, Resource resource) {
        System.out.print(">> Saving resource "+ resource.getUuid_resource() +" into device "+ uuidDevice +" in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", uuidDevice);
        Bson projection = excludeId();
        Document device = collectionDevices.find(filter).projection(projection).first();
        List<Document> resources = null;

        if(device != null){
            resources = (List<Document>) device.get("resources");
            for (Document docResource :
                    resources) {
                if (docResource.getString("uuid_resource").equals(resource.getUuid_resource()))
                    return;

            }

            Document newResource = createDocument(resource);
            resources.add(newResource);
            device.replace("resources", resources);
            ReplaceOptions options = new ReplaceOptions().upsert(true);
            collectionDevices.replaceOne(filter, device, options);
        }
        System.out.println(" ...OK");
    }

    @Override
    public void removeResource(String uuidDevice, Resource resource) {
        removeResourceByUUID(uuidDevice, resource.getUuid_resource());
    }

    @Override
    public void removeResourceByUUID(String uuidDevice, String uuid_resource) {
        System.out.print(">> Removing resource " + uuid_resource + " into device " + uuidDevice + " in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", uuidDevice);
        Document device = collectionDevices.find(filter).projection(excludeId()).first();
        List<Document> resources;
        if (device != null) {
            resources = (List<Document>) device.get("resources");
            resources.forEach(r -> {
                if (r.get("uuid_resource").equals(uuid_resource)) {
                    resources.remove(r);
                }
            });
            device.replace("resources", resources);
            ReplaceOptions options = new ReplaceOptions().upsert(true);
            collectionDevices.replaceOne(filter, device, options);
            System.out.println(" ...OK");
        }
    }

    @Override
    public void updateDevice(Device device) {
        System.out.print(">> Updating device "+ device.getUuid_device() +" in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", device.getUuid_device());
        UpdateOptions options = new UpdateOptions().upsert(true);
        collectionDevices.updateOne(filter, createDocument(device), options);
        System.out.println(" ...OK");
    }

    @Override
    public void updateResource(String uuidDevice, Resource resource) {
        System.out.print(">> Removing resource " + resource.getUuid_resource() + " into device " + uuidDevice + " in MongoDB... ");
        Bson filter = Filters.eq("uuid_device", uuidDevice);
        Document device = collectionDevices.find(filter).projection(excludeId()).first();
        List<Document> resources;
        if (device != null) {
            resources = (List<Document>) device.get("resources");
            resources.forEach(r -> {
                if (r.get("uuid_resource").equals(resource.getUuid_resource())) {
                    resources.remove(r);
                }
            });
            System.out.println(createDocument(resource).toJson());
            resources.add(createDocument(resource));
            device.replace("resources", resources);
            ReplaceOptions options = new ReplaceOptions().upsert(true);
            collectionDevices.replaceOne(filter, device, options);
            System.out.println(" ...OK");
        }
    }

    @Override
    public String setOrGetFogUUIDFog(String uuid_fog) {
        System.out.println(">> Verifying if this node is registered on Storage DB.");
        Document docFog = database.getCollection("fog_computing").find().first();
        if(docFog != null){
            return docFog.getString("uuid_fog");
        }else {
            Document document = new Document();
            document.append("uuid_fog", uuid_fog);
            database.getCollection("fog_computing").insertOne(document);
            return uuid_fog;
        }
    }

    private Document createDocument(Device device){
        Document document = new Document();
        document.append("uuid_device", device.getUuid_device());
        document.append("location", createDocument(device.getLocation()));
        document.append("resources", new ArrayList<Document>());
        return document;
    }

    private Document createDocument(Location location){
        Document document = new Document();
        document.append("lat", location.getLat());
        document.append("lng", location.getLng());
        document.append("alt", location.getAlt());
        return document;
    }

    private Document createDocument(Resource resource){
        Document document = new Document();
        document.append("uuid_resource", resource.getUuid_resource());
        document.append("location", createDocument(resource.getLocation()));
        document.append("services", createDocument(resource.getServices()));
        return document;
    }

    private List<Document> createDocument(List<Service> serviceList){
        List<Document> documentList = new ArrayList<Document>();
        for (Service s :
                serviceList) {
            Document service = new Document();
            service.append("uuid_service", s.getUuid_service());

            List<Document> dataList = new ArrayList<Document>();

            for (Data data :
                    s.getData()) {
                    Document d = new Document();
                    d.append("tag", data.getTag());
                    d.append("value", data.getValue());
                    dataList.add(d);
            }

            service.append("data", dataList);

            documentList.add(service);
        }
        return documentList;
    }


}

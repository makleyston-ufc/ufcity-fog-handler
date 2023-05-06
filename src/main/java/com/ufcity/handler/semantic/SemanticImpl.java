package com.ufcity.handler.semantic;

import com.bordercloud.sparql.SparqlClient;
import com.bordercloud.sparql.SparqlClientException;
import com.bordercloud.sparql.SparqlResult;
import com.bordercloud.sparql.SparqlResultModel;
import com.bordercloud.sparql.authorization.basic.HTTPBasicAuthSettings;
import com.ufcity.handler.models.Device;
import com.ufcity.handler.models.Resource;

import java.net.URI;
import java.net.URISyntaxException;


public class SemanticImpl extends Semantic{
    SparqlClient sparqlClient;
    private static final String PREFIXES =
            "PREFIX :           <http://purl.org/iot/ontology/iot-stream#> \n" +
            "PREFIX bibo:       <http://purl.org/ontology/bibo/> \n" +
            "PREFIX dc:         <http://purl.org/dc/elements/1.1/> \n" +
            "PREFIX iot-lite:   <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#> \n" +
            "PREFIX iot-stream: <http://purl.org/iot/ontology/iot-stream#> \n" +
            "PREFIX ns:         <http://www.w3.org/2003/06/sw-vocab-status/ns#> \n" +
            "PREFIX owl:        <http://www.w3.org/2002/07/owl#> \n" +
            "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
            "PREFIX rdfs:       <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX schema:     <http://schema.org/> \n" +
            "PREFIX skos:       <http://www.w3.org/2004/02/skos/core#> \n" +
            "PREFIX sosa:       <http://www.w3.org/ns/sosa/> \n" +
            "PREFIX terms:      <http://purl.org/dc/terms/> \n" +
            "PREFIX vann:       <http://purl.org/vocab/vann#>\n" +
            "PREFIX wgs84_pos:  <http://www.w3.org/2003/01/geo/wgs84_pos#> \n" +
            "PREFIX xsd:        <http://www.w3.org/2001/XMLSchema#> ";

    public SemanticImpl(String host, String port, String login, String password) {
        super(host, port);

        try {
            URI endpointRead = new URI("http://"+this.getHost()+":"+this.getPort()+"/ufcity-ont/sparql");
            URI endpointWrite = new URI("http://"+this.getHost()+":"+this.getPort()+"/ufcity-ont/update");

            HTTPBasicAuthSettings auth = new HTTPBasicAuthSettings();
            auth.setLogin(login);
            auth.setPassword(password);

            sparqlClient = new SparqlClient(false);
            sparqlClient.setAuthorizationSettings(auth);

            sparqlClient.setEndpointRead(endpointRead);
            sparqlClient.setEndpointWrite(endpointWrite);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void createSemantic(Device device) {
        //TODO
    }

    @Override
    public void createSemantic(Resource resource) {
//TODO
    }

    @Override
    public void saveDevice(Device device) {
        String queryInsert = PREFIXES +
                "CREATE GRAPH :"+device.getUuid_device()+"; \n" +
                "INSERT DATA {\n" +
                ":"+device.getUuid_device()+" rdf:type sosa:Platform ;\n" +
                "    :id '"+device.getUuid_device()+"' ;\n" +
                "    :label '"+device.getUuid_device()+"' ;\n" +
                "    wgs84_pos:lat '"+device.getLocation().getLat()+"' ;\n" +
                "    wgs84_pos:long '"+device.getLocation().getLng()+"' ;\n" +
                "    wgs84_pos:alt '"+device.getLocation().getAlt()+"' ;\n" +
                "}";
        try {
            System.out.print(">> Saving device semantic data in Fuseki server... ");
            sparqlClient.query(queryInsert);
            System.out.println("...OK");
        } catch (SparqlClientException e) {
            System.err.println("...fail");
            e.printStackTrace();
        }
    }

    @Override
    public void saveResource(String uuidDevice, Resource resource) {
        String insertString = PREFIXES +
                "CREATE GRAPH :"+resource.getUuid_resource()+"; \n" +
                "INSERT DATA { \n" +
                ":"+resource.getUuid_resource()+" rdf:type sosa:Sensor ;\n" +
                "    iot-lite:hasUnit :celsius ;\n" +
                "    :id '"+resource.getUuid_resource()+"' ;\n" +
                "    :label '"+resource.getUuid_resource()+"' ;\n" +
                "    sosa:hasSimpleResult ''^^xsd:decimal ;\n" +
                "    wgs84_pos:lat '123' ;\n" +
                "    wgs84_pos:long '321' ;\n" +
                "    wgs84_pos:alt '222' ;\n" +
                "    :belongsTo :"+uuidDevice+" ;\n" +
                "}";
        try {
            System.out.print(">> Saving resource semantic data in Fuseki server... ");
            sparqlClient.query(insertString);
            System.out.println("... OK");
        } catch (SparqlClientException e) {
            System.err.println("... fail");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeDevice(Device device) {
        String updateString = PREFIXES +
                "DELETE DATA {\n" +
                ":"+device.getUuid_device()+" rdf:type sosa:Platform ;\n" +
                "    :id '"+device.getUuid_device()+"' ;\n" +
                "    :label '"+device.getUuid_device()+"' ;\n" +
                "    wgs84_pos:lat '"+device.getLocation().getLat()+"' ;\n" +
                "    wgs84_pos:long '"+device.getLocation().getLng()+"' ;\n" +
                "    wgs84_pos:alt '"+device.getLocation().getAlt()+"' ;\n" +
                "}";
        try {
            System.out.print(">> Removing device semantic data in Fuseki server... ");
            sparqlClient.query(updateString);
            System.out.println("... OK");
        } catch (SparqlClientException e) {
            System.err.println("... fail");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeResource(String uuidDevice, Resource resource) {
        String updateString = PREFIXES +
                "DELETE DATA { \n" +
                ":"+resource.getUuid_resource()+" rdf:type sosa:Sensor ;\n" +
                "    iot-lite:hasUnit :celsius ;\n" +
                "    :id '"+resource.getUuid_resource()+"' ;\n" +
                "    :label '"+resource.getUuid_resource()+"' ;\n" +
                "    sosa:hasSimpleResult ''^^xsd:decimal ;\n" +
                "    wgs84_pos:lat '123' ;\n" +
                "    wgs84_pos:long '321' ;\n" +
                "    wgs84_pos:alt '222' ;\n" +
                "    :belongsTo :"+uuidDevice+" ;\n" +
                "}";
        try {
            System.out.print(">> Saving resource semantic data in Fuseki server... ");
            sparqlClient.query(updateString);
            System.out.println("... OK");
        } catch (SparqlClientException e) {
            System.err.println("... fail");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeResourceByUUID(String uuidDevice, String uuid_resource) {
        Resource r = new Resource();
        r.setUuid_resource(uuid_resource);
        removeResource(uuidDevice, r);
    }

    @Override
    public void updateDevice(Device device) {
        removeDevice(device);
        saveDevice(device);
    }

    @Override
    public void updateResource(String uuidDevice, Resource resource) {
        removeResource(uuidDevice, resource);
        saveResource(uuidDevice, resource);
    }
}

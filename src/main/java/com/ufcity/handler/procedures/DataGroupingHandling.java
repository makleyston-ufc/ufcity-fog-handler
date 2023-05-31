package com.ufcity.handler.procedures;

import com.ufcity.handler.model.Data;
import ufcitycore.models.Resource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ufcity.handler.procedures.METHODS.*;

public class DataGroupingHandling {

    private static DataGroupingHandling instance = null;
    private char method;
    private long size;
    private long time;

    private DataGroupingHandling(){}

    public static DataGroupingHandling getInstance(){
        if(instance == null){
            instance = new DataGroupingHandling();
        }
        return instance;
    }

    public char getMethod() {
        return method;
    }

    public void setMethod(char method) {
        this.method = method;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    List<List<Data>> allQueue = new ArrayList<>();

    public void addData(Resource resource){
        boolean flag = false;
        for (List<Data> oneQueue : allQueue) {
            if(oneQueue.get(0).getResource().getUuid_resource().equals(resource.getUuid_resource())){
                Data data = new Data(resource, LocalDateTime.now());
                oneQueue.add(data);
                flag = true;
            }
        }
        if (!flag) {
            List<Data> oneNewQueue = new ArrayList<>();
            Data data = new Data(resource, LocalDateTime.now());
            oneNewQueue.add(data);
            allQueue.add(oneNewQueue);
        }
        System.out.println("Queues: ");
        for (int i = 0; i < allQueue.size(); i++){
            System.out.println("Size of queue " + i + " # "
                    + allQueue.get(i).get(0).getResource().getUuid_resource()
                    + ": " + allQueue.get(i).size());
        }
    }

    public List<List<Data>> getQueuesToPublish(){
        if (method == methodMap.get("FIXED_SIZE_GROUPING")) {
            return fixedSizeGrouping();
        } else if (method == methodMap.get("HAPPENS_FIRST_GROUPING")) {
            return happensFirstGrouping();
        } else if (method == methodMap.get("AT_LEAST_TIME_GROUPING")) {
            return atLeastTimeGrouping();
        } else if (method == methodMap.get("AT_LEAST_TIME_AND_SIZE_GROUPING")) {
            return atLeastTimeAndSizeGrouping();
        } else {
            return new ArrayList<>();
        }
    }

    private List<List<Data>> atLeastTimeAndSizeGrouping() {
        List<List<Data>> queueToPublish = new ArrayList<>();
        List<List<Data>> queuesToRemove = new ArrayList<>();

        for (List<Data> oneQueue : allQueue) {
            LocalDateTime firstTime = oneQueue.get(0).getLocalDateTime();
            LocalDateTime lastTime = oneQueue.get(oneQueue.size() - 1).getLocalDateTime();
            Duration duration = Duration.between(firstTime, lastTime);
            long seconds = duration.getSeconds();

            if (seconds >= this.time && oneQueue.size() >= this.size) {
                queueToPublish.add(oneQueue);
                queuesToRemove.add(oneQueue);
            }
        }

        allQueue.removeAll(queuesToRemove);

        return queueToPublish;
    }


    private List<List<Data>> atLeastTimeGrouping() {
        List<List<Data>> queueToPublish = new ArrayList<>();
        List<List<Data>> queuesToRemove = new ArrayList<>();

        for (List<Data> oneQueue : allQueue) {
            LocalDateTime firstTime = oneQueue.get(0).getLocalDateTime();
            LocalDateTime lastTime = oneQueue.get(oneQueue.size() - 1).getLocalDateTime();
            Duration duration = Duration.between(firstTime, lastTime);
            long seconds = duration.getSeconds();

            if (seconds >= this.time) {
                queueToPublish.add(oneQueue);
                queuesToRemove.add(oneQueue);
            }
        }

        allQueue.removeAll(queuesToRemove);

        return queueToPublish;
    }


    private List<List<Data>> happensFirstGrouping() {
        List<List<Data>> queueToPublish = new ArrayList<>();
        List<List<Data>> queuesToRemove = new ArrayList<>();

        for (List<Data> oneQueue : allQueue) {
            LocalDateTime firstTime = oneQueue.get(0).getLocalDateTime();
            LocalDateTime lastTime = oneQueue.get(oneQueue.size() - 1).getLocalDateTime();
            Duration duration = Duration.between(firstTime, lastTime);
            long seconds = duration.getSeconds();

            if (seconds >= this.time || oneQueue.size() >= this.size) {
                queueToPublish.add(oneQueue);
                queuesToRemove.add(oneQueue);
            }
        }

        allQueue.removeAll(queuesToRemove);

        return queueToPublish;
    }


    private List<List<Data>> fixedSizeGrouping() {
        List<List<Data>> queueToPublish = new ArrayList<>();
        List<List<Data>> queuesToRemove = new ArrayList<>();

        for (List<Data> oneQueue : allQueue) {
            if (oneQueue.size() >= this.size) {
                queueToPublish.add(oneQueue);
                queuesToRemove.add(oneQueue);
            }
        }

        allQueue.removeAll(queuesToRemove);

        return queueToPublish;
    }

}

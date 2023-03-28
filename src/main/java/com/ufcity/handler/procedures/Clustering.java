package com.ufcity.handler.procedures;

import com.ufcity.handler.models.Resource;

public class Clustering {

    static Clustering instance = null;
    private int qtResource = 10;

    private Clustering(){
    }

    static public Clustering getInstance(){
        if(instance == null)
            instance = new Clustering();
        return instance;
    }

    public Resource clustering(int qtResources){
        //TODO
        return null;
    }

}

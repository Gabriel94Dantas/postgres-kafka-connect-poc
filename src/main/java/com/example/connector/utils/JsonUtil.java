package com.example.connector.utils;

import com.google.gson.Gson;


public class JsonUtil<T> {
    
    private Gson gson;

    
    public JsonUtil(){
        this.gson = new Gson();
    }

    public String toJson(T o){
        return gson.toJson(o);
    }

    public T toObject(String s, Class<T> clazz){
        return gson.fromJson(s, clazz);
    }
}
package com.example.com.utils;

import com.google.gson.Gson;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonUtil<T> {
    
    public String objectToJson(T o){
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public T jsonToObject(String s, Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(s , clazz);
    }

}

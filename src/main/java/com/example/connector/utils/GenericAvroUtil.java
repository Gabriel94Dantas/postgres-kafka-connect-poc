package com.example.connector.utils;

import com.example.connector.dtos.GenericAvroDto;

public class GenericAvroUtil <T> {

    public GenericAvroDto getGenericAvroDto(T data, String endpoint, String origin){
        JsonUtil<T> jsonUtil = new JsonUtil<T>();
        GenericAvroDto genericAvroDto = new GenericAvroDto();
        genericAvroDto.setF1Endpoint(endpoint);
        genericAvroDto.setF2Value(jsonUtil.toJson(data));
        genericAvroDto.setF3SchemaRecord("failSchema");
        genericAvroDto.setF4Origin(origin);
        return genericAvroDto;
    }

}
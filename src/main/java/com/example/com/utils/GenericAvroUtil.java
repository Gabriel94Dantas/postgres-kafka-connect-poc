package com.example.com.utils;

import com.example.com.dtos.GenericAvroDto;

public class GenericAvroUtil {
    
    public GenericAvroDto generateGenericAvroDto(Object o, String tableName){
        try {
            JsonUtil<Object> jsonUtil = new JsonUtil<Object>();
            GenericAvroDto genericAvroDto = new GenericAvroDto();
            genericAvroDto.setF1Endpoint(tableName);
            genericAvroDto.setF2Value(jsonUtil.objectToJson(o));
            genericAvroDto.setF3SchemaRecord("failRecord");
            genericAvroDto.setF4Origin("Test place");
            return genericAvroDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

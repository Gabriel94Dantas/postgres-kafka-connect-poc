package com.example.com.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenericAvroDto {
    
    private String f1Endpoint;

    private String f2Value;

    private String f3SchemaRecord;

    private String f4Origin;

}

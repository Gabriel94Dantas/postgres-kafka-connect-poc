package com.example.connector.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GenericAvroDto{

    @Getter
    @Setter
    private String f1Endpoint;

    @Getter
    @Setter
    private String f2Value;

    @Getter
    @Setter
    private String f3SchemaRecord;

    @Getter
    @Setter
    private String f4Origin;
}
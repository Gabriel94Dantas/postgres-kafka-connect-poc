package com.example.com.serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class AvroSerializer<T> implements Serializer<T> {
    
    @Override
    public byte[] serialize(String topic, T data){
        try {
            byte[] bytes = null;
            if(data != null){
                final Schema schema = ReflectData.get().getSchema(data.getClass());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

                DatumWriter<T> datumWriter = new ReflectDatumWriter<T>(schema);
                datumWriter.write(data, binaryEncoder);

                binaryEncoder.flush();
                byteArrayOutputStream.close();

                bytes = byteArrayOutputStream.toByteArray();
            }
            return bytes;
        } catch (IOException e) {
            throw new SerializationException("Cannot Serialize this data:" + data, e);        
        }
    }

}

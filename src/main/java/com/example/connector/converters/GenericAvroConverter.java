package com.example.connector.converters;

import io.confluent.connect.avro.AvroData;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.apache.avro.SchemaParseException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.storage.Converter;

import com.example.connector.dtos.GenericAvroDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericAvroConverter implements Converter{

    private Integer schemaCacheSize = 50;
    private org.apache.avro.Schema avroSchema = null;

    @SuppressWarnings("unused")
    private Schema connectSchema = null;
    private AvroData avroDataHelper = null;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (configs.get("schema.cache.size") instanceof Integer) {
            schemaCacheSize = (Integer) configs.get("schema.cache.size");
        }

        avroDataHelper = new AvroData(schemaCacheSize);
        try {
            avroSchema = ReflectData.get().getSchema(GenericAvroDto.class);
            connectSchema = avroDataHelper.toConnectSchema(avroSchema);
        } catch (SchemaParseException spe) {
            throw new IllegalStateException("Unable to parse Avro schema when starting GenericAvroConverter", spe);
        }
    }

    @Override
    public byte[] fromConnectData(String topic, Schema schema, Object value) {
        DatumWriter<GenericRecord> datumWriter;
        if (avroSchema != null) {
            datumWriter = new GenericDatumWriter<GenericRecord>(avroSchema);
        } else {
            datumWriter = new GenericDatumWriter<GenericRecord>();
        }
        
        GenericRecord avroInstance = (GenericRecord)avroDataHelper.fromConnectData(schema, value);

        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
        ) {
            dataFileWriter.setCodec(CodecFactory.nullCodec());

            if (avroSchema != null) {
                dataFileWriter.create(avroSchema, baos);
            } else {
                dataFileWriter.create(avroInstance.getSchema(), baos);
            }

            dataFileWriter.append(avroInstance);
            dataFileWriter.flush();

            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new DataException("Error serializing Avro", ioe);
        }
    }

    @Override
    public SchemaAndValue toConnectData(String topic, byte[] value) {
        DatumReader<GenericRecord> datumReader;
        if (avroSchema != null) {
            datumReader = new GenericDatumReader<>(avroSchema);
        } else {
            datumReader = new GenericDatumReader<>();
        }
        GenericRecord instance = null;

        try (
            SeekableByteArrayInput sbai = new SeekableByteArrayInput(value);
            DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(sbai, datumReader);
        ) {
            instance = dataFileReader.next(instance);
            if (instance == null) {
                log.warn("Instance was null");
            }

            if (avroSchema != null) {
                return avroDataHelper.toConnectData(avroSchema, instance);
            } else {
                if(instance != null){
                    return avroDataHelper.toConnectData(instance.getSchema(), instance);
                } else {
                    throw new NullPointerException();
                }
            }
        } catch (IOException ioe) {
            throw new DataException("Failed to deserialize Avro data from topic %s :" + String.format(topic), ioe);
        }
    }
}
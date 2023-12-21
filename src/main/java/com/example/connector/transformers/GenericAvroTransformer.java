package com.example.connector.transformers;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;

import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

import com.example.connector.utils.GenericAvroUtil;
import com.example.connector.dtos.GenericAvroDto;

public abstract class GenericAvroTransformer<R extends ConnectRecord<R>> implements Transformation<R>{

    public static final String OVERVIEW_DOC =
    "Put the data into GenericAvroFormat";

    private interface ConfigName {
        String GENERIC_ORIGIN = "generic.origin";
        String GENERIC_ENDPOINT = "generic.endpoint";
    }

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(ConfigName.GENERIC_ORIGIN, ConfigDef.Type.STRING, "origin", ConfigDef.Importance.HIGH,
        "Generic Avro origin system")
        .define(ConfigName.GENERIC_ENDPOINT, ConfigDef.Type.STRING, "endpoint", ConfigDef.Importance.HIGH,
        "Table name without schema");

    @SuppressWarnings("unused")
    private static final String PURPOSE = "Put the data in the correct format";

    private String origin;
    private String endpoint;

    @SuppressWarnings("unused")
    private Cache<Schema, Schema> schemaUpdateCache;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        origin = config.getString(ConfigName.GENERIC_ORIGIN);
        endpoint = config.getString(ConfigName.GENERIC_ENDPOINT);

        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<Schema, Schema>(16));
    }

    @Override
    public R apply(R record) {
        GenericAvroUtil<R> genericAvroUtil = new GenericAvroUtil<R>();
        GenericAvroDto genericAvroDto = genericAvroUtil.getGenericAvroDto(record, endpoint, origin);
        return newRecord(record, null, genericAvroDto); 
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {
        schemaUpdateCache = null;
    }
    
    protected abstract Schema operatingSchema(R record);

    protected abstract Object operatingValue(R record);

    protected abstract R newRecord(R record, Schema updatedSchema, Object updatedValue);

    public static class Key<R extends ConnectRecord<R>> extends GenericAvroTransformer<R> {

        @Override
        protected Schema operatingSchema(R record) {
            return record.keySchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.key();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), updatedSchema, updatedValue, record.valueSchema(), record.value(), record.timestamp());
        }

    }

    public static class Value<R extends ConnectRecord<R>> extends GenericAvroTransformer<R> {

        @Override
        protected Schema operatingSchema(R record) {
            return record.valueSchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.value();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp());
        }
    }
} 
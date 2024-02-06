package by.aurorasoft.kafka.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonPojoDeserializer<T> implements Deserializer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> targetType;

    public JsonPojoDeserializer(ObjectMapper objectMapper, Class<T> targetType) {
        this.objectMapper = objectMapper;
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, targetType);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }

    @Override
    public void close() {
    }
}

package by.aurorasoft.kafka.stream.deserializer;

import by.aurorasoft.kafka.serialize.AvroGenericRecordDeserializer;
import org.apache.avro.Schema;

import java.util.Map;

public abstract class KafkaStreamAbstractAvroDeserializer extends AvroGenericRecordDeserializer {

    public KafkaStreamAbstractAvroDeserializer(Schema schema) {
        this.schema = schema;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

}

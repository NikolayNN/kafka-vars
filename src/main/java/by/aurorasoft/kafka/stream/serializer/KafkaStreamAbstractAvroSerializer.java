package by.aurorasoft.kafka.stream.serializer;

import by.aurorasoft.kafka.serialize.AvroGenericRecordSerializer;
import org.apache.avro.Schema;

import java.util.Map;

public abstract class KafkaStreamAbstractAvroSerializer extends AvroGenericRecordSerializer {

    public KafkaStreamAbstractAvroSerializer(Schema schema) {
        this.schema = schema;
    }

    @Override
    public void configure(Map<String, ?> arg0, boolean arg1) {
    }
}

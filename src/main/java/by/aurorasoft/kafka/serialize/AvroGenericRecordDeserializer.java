package by.aurorasoft.kafka.serialize;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

//https://medium.com/@mailshine/apache-avro-quick-example-in-kafka-7b2909396c02
public class AvroGenericRecordDeserializer<T extends SpecificRecordBase> implements Deserializer {
    private Schema schema = null;
    protected final Class<T> targetType;

    public AvroGenericRecordDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        schema = (Schema) configs.get("SCHEMA");
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        T result = null;
        try {
            if (bytes != null) {
                DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);
                Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
                result = (T) datumReader.read(null, decoder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {
        // do nothing
    }
}

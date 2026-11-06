package by.aurorasoft.kafka.serialize;

import by.aurorasoft.kafka.model.MessageTransportable;
import by.aurorasoft.kafka.schema.SchemaRepositoryMessageTransportable;
import by.aurorasoft.kafka.variables.KafkaVars;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

//https://medium.com/@mailshine/apache-avro-quick-example-in-kafka-7b2909396c02
public class AvroGenericRecordDeserializer implements Deserializer<GenericRecord> {

    protected Schema schema = null;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        schema = (Schema) configs.get(KafkaVars.SCHEMA_PROP_NAME);
    }

    @Override
    public GenericRecord deserialize(String topic, byte[] bytes) {
        GenericRecord result = null;
        try {
            if (bytes != null) {
                if (MessageTransportable.class.getName().equals(schema.getFullName())
                        && schema.getField("strParams") != null) {
                    return readMessage(bytes);
                }
                DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
                Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
                result = datumReader.read(null, decoder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private GenericRecord readMessage(byte[] bytes) throws IOException {
        Schema legacySchema = SchemaRepositoryMessageTransportable.getLegacySchemaObject();
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        GenericRecord record = new GenericDatumReader<GenericRecord>(legacySchema, schema)
                .read(null, decoder);
        // Old records end after the legacy fields; new records append one Avro map.
        if (!decoder.isEnd()) {
            Object strParams = new GenericDatumReader<>(schema.getField("strParams").schema())
                    .read(null, decoder);
            record.put("strParams", strParams);
            if (!decoder.isEnd()) {
                throw new IOException("Unexpected bytes after message string parameters");
            }
        }
        return record;
    }

    @Override
    public void close() {
        // do nothing
    }
}

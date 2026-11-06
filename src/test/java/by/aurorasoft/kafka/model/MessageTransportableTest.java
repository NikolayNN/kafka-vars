package by.aurorasoft.kafka.model;

import by.aurorasoft.kafka.schema.SchemaRepositoryMessageTransportable;
import by.aurorasoft.kafka.serialize.AvroGenericRecordDeserializer;
import by.aurorasoft.kafka.serialize.AvroGenericRecordSerializer;
import by.aurorasoft.kafka.utils.GenericRecordFactory;
import by.aurorasoft.kafka.variables.KafkaVars;
import by.nhorushko.aurutils.message.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MessageTransportableTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Schema schema = SchemaRepositoryMessageTransportable.getSchemaObject();

    @Test
    public void schemaDefinesStringMapWithEmptyDefault() {
        Schema.Field field = schema.getField("strParams");

        assertNotNull("Message Avro schema must include strParams", field);
        assertEquals(Schema.Type.MAP, field.schema().getType());
        assertEquals(Schema.Type.STRING, field.schema().getValueType().getType());
        assertEquals(Collections.emptyMap(), field.defaultVal());
    }

    @Test
    public void stringIdentifiersSurviveJsonAndAvroSerialization() throws IOException {
        MessageTransportable message = readMessage(
                "{\"ble_1_id\":\"0012AB34CD56\",\"ble_2_id\":\"AABBCCDDEEFF\"}");

        GenericRecord decoded = roundTrip(message);

        assertEquals("ble_1_rssi:-65,ble_1_battery:90", decoded.get("params").toString());
        assertEquals(42L, decoded.get("unitId"));
        Map<?, ?> strParams = (Map<?, ?>) decoded.get("strParams");
        assertEquals(2, strParams.size());
        assertEquals("0012AB34CD56", valueFor(strParams, "ble_1_id"));
        assertEquals("AABBCCDDEEFF", valueFor(strParams, "ble_2_id"));
    }

    @Test
    public void missingStringParametersSerializeAsEmptyMap() throws IOException {
        GenericRecord decoded = roundTrip(readMessage(null));

        assertEquals(Collections.emptyMap(), decoded.get("strParams"));
    }

    @Test
    public void nullStringParametersSerializeAsEmptyMap() throws IOException {
        GenericRecord decoded = roundTrip(readMessage("null"));

        assertEquals(Collections.emptyMap(), decoded.get("strParams"));
    }

    @Test
    public void legacyConstructorSerializesEmptyStringParameters() {
        MessageTransportable message = new MessageTransportable(
                null, 0L, 0F, 0F, 0, 0, 0, 0, 0D, 0, 0, 0,
                0F, 0F, 0F, 0F, 0F, "", 42L, false, MessageType.values()[0]);

        assertEquals(Collections.emptyMap(), roundTrip(message).get("strParams"));
    }

    @Test
    public void readerUsesEmptyMapForMessagesWrittenWithLegacySchema() throws IOException {
        assertNotNull("Message Avro schema must include strParams", schema.getField("strParams"));
        ObjectNode legacySchemaJson = (ObjectNode) objectMapper.readTree(schema.toString());
        ArrayNode fields = (ArrayNode) legacySchemaJson.get("fields");
        for (int index = fields.size() - 1; index >= 0; index--) {
            if (fields.get(index).get("name").asText().equals("strParams")) {
                fields.remove(index);
            }
        }
        Schema legacySchema = new Schema.Parser().parse(legacySchemaJson.toString());
        GenericRecord legacyRecord = GenericRecordFactory.createGenericRecord(readMessage(null), legacySchema);
        AvroGenericRecordSerializer serializer = new AvroGenericRecordSerializer();
        serializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, legacySchema), false);
        byte[] bytes = serializer.serialize("messages", legacyRecord);
        assertNotNull(bytes);

        GenericRecord decoded = new GenericDatumReader<GenericRecord>(legacySchema, schema)
                .read(null, DecoderFactory.get().binaryDecoder(bytes, null));

        assertEquals(Collections.emptyMap(), decoded.get("strParams"));
        assertEquals("ble_1_rssi:-65,ble_1_battery:90", decoded.get("params").toString());
        assertEquals(42L, decoded.get("unitId"));
    }

    private MessageTransportable readMessage(String strParamsJson) throws IOException {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("params", "ble_1_rssi:-65,ble_1_battery:90");
        json.put("unitId", 42L);
        json.put("type", MessageType.values()[0].name());
        if (strParamsJson != null) {
            json.set("strParams", objectMapper.readTree(strParamsJson));
        }
        return objectMapper.treeToValue(json, MessageTransportable.class);
    }

    private GenericRecord roundTrip(MessageTransportable message) {
        GenericRecord record = GenericRecordFactory.createGenericRecord(message, schema);
        AvroGenericRecordSerializer serializer = new AvroGenericRecordSerializer();
        serializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, schema), false);
        byte[] bytes = serializer.serialize("messages", record);
        assertNotNull("Avro serialization must produce a payload", bytes);
        assertTrue(bytes.length > 0);
        AvroGenericRecordDeserializer deserializer = new AvroGenericRecordDeserializer();
        deserializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, schema), false);
        GenericRecord decoded = deserializer.deserialize("messages", bytes);
        assertNotNull("Avro deserialization must restore the message", decoded);
        return decoded;
    }

    private String valueFor(Map<?, ?> values, String key) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey().toString().equals(key))
                .map(entry -> entry.getValue().toString())
                .findFirst().orElse(null);
    }
}

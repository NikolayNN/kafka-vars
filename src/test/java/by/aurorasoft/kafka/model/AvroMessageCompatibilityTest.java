package by.aurorasoft.kafka.model;

import by.aurorasoft.kafka.producer.KafkaProducerGenericRecordSimple;
import by.aurorasoft.kafka.schema.SchemaRepositoryMessageTransportable;
import by.aurorasoft.kafka.serialize.AvroGenericRecordDeserializer;
import by.aurorasoft.kafka.serialize.AvroGenericRecordSerializer;
import by.aurorasoft.kafka.stream.deserializer.KafkaStreamAbstractAvroDeserializer;
import by.aurorasoft.kafka.utils.GenericRecordFactory;
import by.aurorasoft.kafka.variables.KafkaVars;
import by.nhorushko.aurutils.message.MessageType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AvroMessageCompatibilityTest {

    private static final String LEGACY_PAYLOAD =
            "yAEYtAHoOaYEzcxMPpqZmT4AAAA/zczMPgAAAADAHMhAlgEC9gEBAABSQgAAvkEAAEhB"
                    + "PmJsZV8xX3Jzc2k6LTY1LGJsZV8xX2JhdHRlcnk6OTCuAYDS7fEMAFQ=";

    private final Schema currentSchema = SchemaRepositoryMessageTransportable.getSchemaObject();
    private final Schema legacySchema = SchemaRepositoryMessageTransportable.getLegacySchemaObject();

    @Test
    public void readsFrozenLegacyPayloadWithCurrentDeserializer() {
        GenericRecord decoded = deserializer().deserialize("messages", legacyBytes());

        assertLegacyValues(decoded);
        assertEquals(Collections.emptyMap(), decoded.get("strParams"));
    }

    @Test
    public void sameDeserializerReadsOldAndNewMessages() {
        byte[] newBytes = serialize(message(Map.of("ble_1_id", "0012AB34CD56")));

        for (AvroGenericRecordDeserializer deserializer : List.of(deserializer(),
                new KafkaStreamAbstractAvroDeserializer(currentSchema) {
                })) {
            for (int index = 0; index < 4; index++) {
                GenericRecord decoded = deserializer.deserialize("messages", index % 2 == 0 ? legacyBytes() : newBytes);

                assertLegacyValues(decoded);
                Map<?, ?> strParams = (Map<?, ?>) decoded.get("strParams");
                if (index % 2 == 0) {
                    assertEquals(Collections.emptyMap(), strParams);
                } else {
                    assertEquals(1, strParams.size());
                    assertEquals("ble_1_id", strParams.keySet().iterator().next().toString());
                    assertEquals("0012AB34CD56", strParams.values().iterator().next().toString());
                }
            }
        }
    }

    @Test
    public void oldConsumerReadsNewMessagesWithoutChangingLegacyFields() throws IOException {
        byte[] newBytes = serialize(message(Map.of("ble_1_id", "0012AB34CD56")));

        assertArrayEquals(legacyBytes(), Arrays.copyOf(newBytes, legacyBytes().length));
        GenericRecord decoded = new GenericDatumReader<GenericRecord>(legacySchema)
                .read(null, DecoderFactory.get().binaryDecoder(newBytes, null));

        assertLegacyValues(decoded);
    }

    @Test
    public void newMessageWithEmptyMapIsReadableByBothConsumers() throws IOException {
        byte[] newBytes = serialize(message(Collections.emptyMap()));

        assertEquals(legacyBytes().length + 1, newBytes.length);
        assertLegacyValues(new GenericDatumReader<GenericRecord>(legacySchema)
                .read(null, DecoderFactory.get().binaryDecoder(newBytes, null)));
        GenericRecord decoded = deserializer().deserialize("messages", newBytes);
        assertLegacyValues(decoded);
        assertEquals(Collections.emptyMap(), decoded.get("strParams"));
    }

    @Test
    public void reflectionProducerUsesCompatibleFieldOrder() {
        var producer = new KafkaProducerGenericRecordSimple<Long, MessageTransportable>(
                "messages", null, currentSchema) {
            @Override
            public void send(MessageTransportable model) {
                throw new UnsupportedOperationException();
            }

            GenericRecord record(MessageTransportable model) {
                return convertTransportableToTopicValue(model);
            }
        };
        GenericRecord record = producer.record(message(Map.of("ble_1_id", "0012AB34CD56")));
        byte[] newBytes = serializer(currentSchema).serialize("messages", record);

        assertNotNull(newBytes);
        assertArrayEquals(legacyBytes(), Arrays.copyOf(newBytes, legacyBytes().length));
        GenericRecord decoded = deserializer().deserialize("messages", newBytes);
        assertLegacyValues(decoded);
        assertEquals("0012AB34CD56", ((Map<?, ?>) decoded.get("strParams")).values().iterator().next().toString());
    }

    @Test
    public void truncatedMapIsRejectedRatherThanTreatedAsLegacyMessage() {
        byte[] bytes = serialize(message(Map.of("ble_1_id", "0012AB34CD56")));

        assertNull(deserializer().deserialize("messages", Arrays.copyOf(bytes, bytes.length - 2)));
    }

    @Test
    public void extraBytesAfterMapAreRejected() {
        byte[] bytes = serialize(message(Collections.emptyMap()));

        assertNull(deserializer().deserialize("messages", Arrays.copyOf(bytes, bytes.length + 1)));
    }

    @Test
    public void otherRecordSchemasKeepTheirExistingFormat() {
        Schema otherSchema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OtherRecord\","
                + "\"fields\":[{\"name\":\"value\",\"type\":\"string\"}]}");
        GenericRecord record = new GenericData.Record(otherSchema);
        record.put("value", "hello");
        byte[] bytes = serializer(otherSchema).serialize("other", record);
        AvroGenericRecordDeserializer deserializer = new AvroGenericRecordDeserializer();
        deserializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, otherSchema), false);

        assertArrayEquals(new byte[]{10, 'h', 'e', 'l', 'l', 'o'}, bytes);
        assertEquals("hello", deserializer.deserialize("other", bytes).get("value").toString());
    }

    private void assertLegacyValues(GenericRecord decoded) {
        assertNotNull("Consumer must read both retained old messages and new messages", decoded);
        assertEquals(123L, decoded.get("id"));
        assertEquals(1730000000L, decoded.get("time"));
        assertEquals(52.5F, decoded.get("latitude"));
        assertEquals(23.75F, decoded.get("longitude"));
        assertEquals(100, decoded.get("altitude"));
        assertEquals(87, decoded.get("speed"));
        assertEquals(12, decoded.get("amountSatellite"));
        assertEquals(275, decoded.get("course"));
        assertEquals(12345.5D, decoded.get("gpsOdometer"));
        assertEquals(75, decoded.get("gsmStrength"));
        assertEquals(90, decoded.get("battery"));
        assertEquals(3700, decoded.get("batteryVoltage"));
        assertEquals(12.5F, decoded.get("onboardVoltage"));
        assertEquals(0.2F, decoded.get("ecoAcceleration"));
        assertEquals(0.3F, decoded.get("ecoBraking"));
        assertEquals(0.4F, decoded.get("ecoCornering"));
        assertEquals(0.5F, decoded.get("ecoBump"));
        assertEquals("ble_1_rssi:-65,ble_1_battery:90", decoded.get("params").toString());
        assertEquals(42L, decoded.get("unitId"));
        assertEquals(true, decoded.get("isArchive"));
        assertEquals("ERROR", decoded.get("type").toString());
    }

    private MessageTransportable message(Map<String, String> strParams) {
        return new MessageTransportable(
                123L, 1730000000L, 52.5F, 23.75F, 100, 87, 12, 275,
                12345.5D, 75, 90, 3700, 12.5F, 0.2F, 0.3F, 0.4F, 0.5F,
                "ble_1_rssi:-65,ble_1_battery:90", strParams, 42L, true, MessageType.ERROR);
    }

    private byte[] legacyBytes() {
        return Base64.getDecoder().decode(LEGACY_PAYLOAD);
    }

    private byte[] serialize(MessageTransportable message) {
        byte[] bytes = serializer(currentSchema).serialize("messages",
                GenericRecordFactory.createGenericRecord(message, currentSchema));
        assertNotNull(bytes);
        return bytes;
    }

    private AvroGenericRecordSerializer serializer(Schema schema) {
        AvroGenericRecordSerializer serializer = new AvroGenericRecordSerializer();
        serializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, schema), false);
        return serializer;
    }

    private AvroGenericRecordDeserializer deserializer() {
        AvroGenericRecordDeserializer deserializer = new AvroGenericRecordDeserializer();
        deserializer.configure(Map.of(KafkaVars.SCHEMA_PROP_NAME, currentSchema), false);
        return deserializer;
    }
}

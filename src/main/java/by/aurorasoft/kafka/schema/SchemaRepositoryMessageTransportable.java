package by.aurorasoft.kafka.schema;

import by.aurorasoft.kafka.model.MessageTransportable;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SchemaRepositoryMessageTransportable {
    private static final Schema LEGACY_SCHEMA = loadLegacySchema();
    private static final Schema SCHEMA = createSchema();

    public static Schema getSchemaObject() {
        return SCHEMA;
    }

    public static Schema getLegacySchemaObject() {
        return LEGACY_SCHEMA;
    }

    private static Schema loadLegacySchema() {
        try (InputStream input = Objects.requireNonNull(
                SchemaRepositoryMessageTransportable.class.getResourceAsStream(
                        "/avro/message-transportable-v1.avsc"), "Legacy message schema is missing")) {
            return new Schema.Parser().parse(input);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Schema createSchema() {
        Schema reflected = ReflectData.get().getSchema(MessageTransportable.class);
        List<Schema.Field> fields = new ArrayList<>();
        for (Schema.Field legacyField : LEGACY_SCHEMA.getFields()) {
            Schema.Field field = reflected.getField(legacyField.name());
            if (field == null || !field.schema().equals(legacyField.schema())) {
                throw new IllegalStateException("Legacy message field changed: " + legacyField.name());
            }
            fields.add(new Schema.Field(field, field.schema()));
        }
        Schema.Field strParams = reflected.getField("strParams");
        fields.add(new Schema.Field(strParams, strParams.schema()));
        if (fields.size() != reflected.getFields().size()) {
            throw new IllegalStateException("Message schema contains unsupported fields");
        }
        // ReflectData sorts fields by name. Keep the legacy binary prefix intact.
        Schema schema = Schema.createRecord(reflected.getName(), reflected.getDoc(),
                reflected.getNamespace(), reflected.isError());
        schema.setFields(fields);
        reflected.getObjectProps().forEach(schema::addProp);
        return schema;
    }
}

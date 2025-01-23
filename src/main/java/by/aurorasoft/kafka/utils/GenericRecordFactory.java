package by.aurorasoft.kafka.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;

public class GenericRecordFactory {
    public static GenericRecord createGenericRecord(Object obj, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            Object fieldValue = ReflectData.get().getField(obj, field.name(), field.pos());

            if (field.schema().getType() == Schema.Type.ENUM) {
                fieldValue = new GenericData.EnumSymbol(field.schema(), fieldValue.toString());
            }

            record.put(field.name(), fieldValue);
        }
        return record;
    }
}

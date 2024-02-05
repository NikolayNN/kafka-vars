package by.aurorasoft.kafka.consumer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;

public interface GenericRecordFactory {
    default GenericRecord createGenericRecord(Object obj, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            Object fieldValue = ReflectData.get().getField(obj, field.name(), field.pos());
            record.put(field.name(), fieldValue);
        }
        return record;
    }
}

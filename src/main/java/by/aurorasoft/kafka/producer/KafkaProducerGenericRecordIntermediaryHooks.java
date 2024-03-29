package by.aurorasoft.kafka.producer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class KafkaProducerGenericRecordIntermediaryHooks<TOPIC_KEY, TRANSPORTABLE, MODEL> extends KafkaProducerGenericRecordIntermediary<TOPIC_KEY, TRANSPORTABLE, MODEL> {

    public final List<MODEL> failureMessages = Collections.synchronizedList(new ArrayList<>());
    public final ProducerMetrics metrics = new ProducerMetrics();

    public KafkaProducerGenericRecordIntermediaryHooks(String topicName, KafkaTemplate<TOPIC_KEY, GenericRecord> kafkaTemplate, Schema schema) {
        super(topicName, kafkaTemplate, schema);
    }

    @Override
    public void send(MODEL model) {
        onBeforeSend();
        if (isNotSendable(model)) {
            metrics.skipCounterIncrement();
            return;
        }
        sendModel(getTopicKey(model), model)
                .handle((sendResult, throwable) -> {
                    if (throwable != null) {
                        // Обработка исключения
                        failureMessages.add(model);
                        metrics.failureCounterIncrement();
                        onSendFailure(model, throwable);
                    } else {
                        // Обработка успешного результата
                        metrics.successCounterIncrement();
                        onSendSuccess(sendResult);
                    }
                    return null;
                });
        metrics.sentCounterIncrement();
    }

    protected boolean isNotSendable(MODEL model) {
        return false;
    }

    protected abstract TOPIC_KEY getTopicKey(MODEL model);

    protected void onBeforeSend() {
    }

    protected void onSendFailure(MODEL model, Throwable throwable) {
    }

    protected void onSendSuccess(@Nullable SendResult<TOPIC_KEY, GenericRecord> sendResult) {
    }
}

package by.aurorasoft.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.ui.Model;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class KafkaProducerAbstract<TOPIC_KEY, TOPIC_VALUE, TRANSPORTABLE, MODEL> {

    protected final KafkaTemplate<TOPIC_KEY, TOPIC_VALUE> kafkaTemplate;
    protected final String topicName;

    public KafkaProducerAbstract(String topicName, KafkaTemplate<TOPIC_KEY, TOPIC_VALUE> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public abstract ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendGet(MODEL model);

    public void send(MODEL model){
        sendGet(model);
    }

    protected abstract TRANSPORTABLE convertModelToTransportable(MODEL model);

    protected abstract TOPIC_VALUE convertTransportableToTopicValue(TRANSPORTABLE intermediate);

    public List<ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>>> sendGet(Collection<MODEL> models){
        List<ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>>> results = new LinkedList<>();
        for (MODEL model : models) {
            results.add(this.sendGet(model));
        }
        return results;
    }

    protected ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendModel(TOPIC_KEY key, MODEL model) {
        TOPIC_VALUE value = topicValue(model);
        return sendKafka(key, value);
    }

    protected ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendModel(MODEL model) {
        TOPIC_VALUE value = topicValue(model);
        return sendKafka(value);
    }

    protected TOPIC_VALUE topicValue(MODEL model) {
        TRANSPORTABLE transportable = convertModelToTransportable(model);
        return convertTransportableToTopicValue(transportable);
    }

    protected ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendKafka(TOPIC_VALUE value) {
        return sendKafka(new ProducerRecord<>(topicName, value));
    }

    protected ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendKafka(TOPIC_KEY key, TOPIC_VALUE value) {
        return sendKafka(new ProducerRecord<>(topicName, key, value));
    }

    protected ListenableFuture<SendResult<TOPIC_KEY, TOPIC_VALUE>> sendKafka(ProducerRecord<TOPIC_KEY, TOPIC_VALUE> producerRecord) {
        return kafkaTemplate.send(producerRecord);
    }
}

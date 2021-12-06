package by.aurorasoft.kafka.topic;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

public class KafkaAdminFactory {

    public static KafkaAdmin kafkaAdmin(String bootstrapAddress) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
}
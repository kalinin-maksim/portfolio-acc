package edu.kalinin.acc.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.message", ignoreUnknownFields = true)
    public BaseKafkaProperties messageKafkaProperties() {
        var baseKafkaProperties = new BaseKafkaProperties();
        var consumer = new BaseKafkaProperties.Consumer();
        consumer.setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        consumer.setValueDeserializer("io.confluent.kafka.serializers.StringDeserializer");
        baseKafkaProperties.setConsumer(consumer);
        return baseKafkaProperties;
    }

    @Setter
    @Getter
    public static class BaseKafkaProperties {

        private final Map<String, String> properties = new HashMap<>();
        private List<String> bootstrapServers;
        private Consumer consumer = new Consumer();

        public void putTo(Map<String, Object> properties) {
            if (this.bootstrapServers != null) {
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
            }
            if (this.consumer.keyDeserializer != null) {
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, this.consumer.keyDeserializer);
            }
            if (this.consumer.valueDeserializer != null) {
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, this.consumer.valueDeserializer);
            }
            if (this.consumer.groupId != null) {
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.consumer.groupId);
            }
            properties.putAll(this.properties);
        }

        @Setter
        @Getter
        public static class Consumer {
            private String keyDeserializer;
            private String valueDeserializer;
            private String groupId;
        }
    }
}

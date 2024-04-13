package edu.kalinin.acc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

@Profile("kafka-local")
@Configuration
@EmbeddedKafka(topics = "${kafka.message.topic-name}", partitions = 1)
public class EmbeddedKafkaConfiguration {

    @Value("${kafka.message.topic-name}")
    private String topicName;

    @Bean
    EmbeddedKafkaBroker broker() {
        return new EmbeddedKafkaBroker(1)
                .kafkaPorts(9092)
                .brokerListProperty("spring.kafka.bootstrap-servers");
    }
}

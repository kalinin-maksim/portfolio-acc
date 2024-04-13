package edu.kalinin.acc.configuration;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;

import java.util.*;

public abstract class AbstractConsumerInterceptor implements ConsumerInterceptor<Object, Object> {

    protected abstract Object getObject(String key, String value, Headers headers);

    @Override
    public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> consumerRecords) {
        Set<TopicPartition> partitions = consumerRecords.partitions();
        Map<TopicPartition, List<ConsumerRecord<Object, Object>>> listMap = new HashMap<>();

        for (TopicPartition partition : partitions) {
            List<ConsumerRecord<Object, Object>> recordsNew = new ArrayList<>();
            for (ConsumerRecord<Object, Object> consumerRecord : consumerRecords) {
                var message = getObject((String) consumerRecord.key(), (String) consumerRecord.value(), consumerRecord.headers());
                recordsNew.add(new ConsumerRecord<>(partition.topic(), partition.partition(), consumerRecord.offset(), consumerRecord.key(), message));
            }
            listMap.put(partition, recordsNew);
        }
        return new ConsumerRecords<>(listMap);
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // Do nothing
    }
}

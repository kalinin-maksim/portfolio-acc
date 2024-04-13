package edu.kalinin.acc.consumer.util;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface KafkaHelper {

    static Map<String, Class<?>> topicMessageTypes(Class<?> kafkaListenersBean) {
        class Pair {
            final String topic;
            final Class<?> messageType;

            public Pair(String topic, Class<?> messageType) {
                this.topic = topic;
                this.messageType = messageType;
            }
        }

        return Arrays.stream(kafkaListenersBean.getDeclaredMethods())
                .flatMap(method -> {
                    var annotation = method.getAnnotation(KafkaListener.class);
                    if (annotation != null)
                        return Arrays.stream(annotation.topics())
                                .map(topic -> new Pair(topic, method.getParameterTypes()[0]));
                    else return
                            Stream.empty();
                })
                .collect(Collectors.toMap(p -> p.topic, p -> p.messageType));
    }

}

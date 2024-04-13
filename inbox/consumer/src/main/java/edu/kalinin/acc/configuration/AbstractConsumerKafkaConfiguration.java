package edu.kalinin.acc.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;
import edu.kalinin.acc.consumer.util.KafkaHelper;
import edu.kalinin.acc.helper.util.ConstructionHelper;
import edu.kalinin.acc.validation.JsonValidateException;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractConsumerKafkaConfiguration implements EmbeddedValueResolverAware {

    protected final ObjectMapper objectMapper = objectMapper();

    @Resource
    private org.springframework.boot.autoconfigure.kafka.KafkaProperties commonKafkaProperties;
    private StringValueResolver stringValueResolver;

    protected abstract Class<?> getKafkaListenersClass();
    protected abstract edu.kalinin.acc.validation.EventValidator getValidator();
    protected abstract edu.kalinin.acc.validation.JsonValidateErrorHandler getValidateErrorHandler();

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    protected Map<String, Object> consumerConfigs(KafkaConfiguration.BaseKafkaProperties kafkaProperties) {
        var properties = commonKafkaProperties.buildConsumerProperties();
        kafkaProperties.putTo(properties);
        return properties;
    }

    protected StringDeserializer stringDeserializer() {
        return new StringDeserializer();
    }

    protected Deserializer<Object> jsonDeserializer() {
        var topicMessageTypes = KafkaHelper.topicMessageTypes(getKafkaListenersClass());
        return (topic, messageBytes) -> {
            var messageType = getMessageType(topicMessageTypes, topic);
            return parseToObjectFrom(messageBytes, messageType);
        };
    }

    protected Object parseToObjectFrom(byte[] messageBytes, Class<?> messageType) {
        var json = new String(messageBytes, StandardCharsets.UTF_8);
        try {
            var message = getValidator().validate(json, objectMapper);
            return objectMapper.readValue(message, messageType);
        } catch (JsonValidateException e) {
            getValidateErrorHandler().handle(json, e.getMessage());
            return null;
        } catch (Exception ex) {
            log.warn("kafka-message parse cause error" +
                            "\nmessage: {}", new String(messageBytes),
                    ex);
            return null;
        }
    }

    private static ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(ConstructionHelper.let(() -> {
            var javaTimeModule = new JavaTimeModule();
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
            return javaTimeModule;
        }));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    private Class<?> getMessageType(Map<String, Class<?>> topicMessageTypes, String topic) {
        return topicMessageTypes.entrySet().stream()
                .filter(kv -> stringValueResolver.resolveStringValue(kv.getKey()).equals(topic))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }


}

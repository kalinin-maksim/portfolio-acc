package edu.kalinin.acc.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import edu.kalinin.acc.consumer.KafKaEventConsumer;
import edu.kalinin.acc.dto.external.input.LoanMessageDTO;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.validation.EventValidator;
import edu.kalinin.acc.validation.JsonValidateErrorHandler;

@Configuration
@ConditionalOnExpression(value = "${kafka.loan.consumer.enabledExp}")
@Slf4j
public class LoanMessageConsumerKafkaConfiguration extends AbstractConsumerKafkaConfiguration {

    @Lazy
    @Autowired
    @Qualifier("messageKafkaProperties")
    private KafkaConfiguration.BaseKafkaProperties messageKafkaProperties;

    private final JsonValidateErrorHandler validateErrorHandler;

    public LoanMessageConsumerKafkaConfiguration(JsonValidateErrorHandler validateErrorHandler) {
        super();
        this.validateErrorHandler = validateErrorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> loanMessageKafkaListenerContainerFactory() {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        containerFactory.setConsumerFactory(consumerFactory());
        return containerFactory;
    }

    @Override
    protected Class<KafKaEventConsumer> getKafkaListenersClass() {
        return KafKaEventConsumer.class;
    }

    @Override
    protected EventValidator getValidator() {
        return new EventValidator("/schema/external/input/LoanMessageDTO.json");
    }

    @Override
    protected JsonValidateErrorHandler getValidateErrorHandler() {
        return validateErrorHandler;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        var consumerConfigs = consumerConfigs(messageKafkaProperties);
        consumerConfigs.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, MessageConsumerInterceptor.class.getName());
        log.info("messageKafkaProperties: {}", consumerConfigs);
        return new DefaultKafkaConsumerFactory<>(consumerConfigs, stringDeserializer(), stringDeserializer());
    }

    private Object message(String key, String value, Headers headers) {
        return parseToObjectFrom(value.getBytes(), LoanMessageDTO.class);
    }

    public static class MessageConsumerInterceptor extends AbstractConsumerInterceptor {

        LoanMessageConsumerKafkaConfiguration messageHandler = SpringSupport.getContext().getBean(LoanMessageConsumerKafkaConfiguration.class);

        @Override
        protected Object getObject(String key, String value, Headers headers) {
            return messageHandler.message(key, value, headers);
        }
    }
}

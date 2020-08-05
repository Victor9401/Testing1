package bet.olimp.actions;

import bet.olimp.actions.entities.Bet;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;


    public KafkaConfiguration(KafkaProperties kafkaProperties) {

        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaListenerContainerFactory<?> singleFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Bet> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(false);
        factory.setMessageConverter(new StringJsonMessageConverter());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, Bet> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        return new ConcurrentKafkaListenerContainerFactory<>();
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        return new HashMap<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public StringJsonMessageConverter converter() {
        return new StringJsonMessageConverter();
    }
}



package exe2.learningapp.logineko.quizziz.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.config.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

    @Configuration
    public class KafkaConfig {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;
        @Bean
        public ProducerFactory<String, Object> producerFactory() {
            Map<String, Object> cfg = new HashMap<>();
            cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            cfg.put(ProducerConfig.ACKS_CONFIG, "all");
            cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
            cfg.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            return new DefaultKafkaProducerFactory<>(cfg);
        }

        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        @Bean
        public ConsumerFactory<String, Object> consumerFactory() {
            Map<String, Object> cfg = new HashMap<>();
            cfg.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "quiz-service");
            cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            cfg.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
            cfg.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class);
            cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            cfg.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
            return new DefaultKafkaConsumerFactory<>(cfg);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, Object> f = new ConcurrentKafkaListenerContainerFactory<>();
            f.setConsumerFactory(consumerFactory());
            f.setConcurrency(3); // Set concurrency for better performance
            f.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.BATCH);
            f.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler(
                    new org.springframework.util.backoff.FixedBackOff(1000L, 3L)
            ));
            return f;
        }


}
package ru.kolobkevic.tasktracker.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String USER_TOPIC = "user-topic";

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(USER_TOPIC)
                .build();
    }
}

package ru.kolobkevic.tasktracker.scheduler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String EMAIL_SENDING_TASKS = "EMAIL_SENDING_TASKS";

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(EMAIL_SENDING_TASKS)
                .build();
    }
}

package ru.kolobkevic.tasktracker.emailsender.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kolobkevic.tasktracker.emailsender.dto.UserTopicDto;

import static ru.kolobkevic.tasktracker.emailsender.config.KafkaConsumerConfig.GROUP_ID;

@Component
public class KafkaListeners {
    public static final String USER_TOPIC = "user-topic";

    @KafkaListener(topics = USER_TOPIC, groupId = GROUP_ID, containerFactory = "userKafkaListenerContainerFactory")
    public void listener(UserTopicDto userTopicDto) {
    }

}

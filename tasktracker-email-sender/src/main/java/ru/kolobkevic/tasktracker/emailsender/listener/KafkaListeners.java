package ru.kolobkevic.tasktracker.emailsender.listener;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kolobkevic.tasktracker.emailsender.dto.UserTopicDto;
import ru.kolobkevic.tasktracker.emailsender.service.MailService;

import static ru.kolobkevic.tasktracker.emailsender.config.KafkaConsumerConfig.GROUP_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {
    public static final String USER_TOPIC = "user-topic";
    private final MailService mailService;

    @KafkaListener(topics = USER_TOPIC, groupId = GROUP_ID, containerFactory = "userKafkaListenerContainerFactory")
    public void listener(UserTopicDto userTopicDto) {
        try {
            log.info("Trying to send message to {}, email {}", userTopicDto.getUsername(), userTopicDto.getEmail());
            mailService.sendWelcomeMail(userTopicDto);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}

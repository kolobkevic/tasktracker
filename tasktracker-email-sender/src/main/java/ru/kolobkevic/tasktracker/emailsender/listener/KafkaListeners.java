package ru.kolobkevic.tasktracker.emailsender.listener;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kolobkevic.tasktracker.emailsender.dto.EmailSendingDto;
import ru.kolobkevic.tasktracker.emailsender.service.MailService;

import static ru.kolobkevic.tasktracker.emailsender.config.KafkaConsumerConfig.GROUP_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {
    public static final String EMAIL_SENDING_TASKS = "EMAIL_SENDING_TASKS";
    private final MailService mailService;

    @KafkaListener(topics = EMAIL_SENDING_TASKS, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void listener(EmailSendingDto emailSendingDto) {
        try {
            log.info("Trying to send message {} with title {} to {}",
                    emailSendingDto.getContent(), emailSendingDto.getTitle(), emailSendingDto.getEmail());
            mailService.sendMail(emailSendingDto);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

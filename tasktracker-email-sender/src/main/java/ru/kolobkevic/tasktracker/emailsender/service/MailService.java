package ru.kolobkevic.tasktracker.emailsender.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.emailsender.dto.EmailSendingDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    @Value("${spring.mail.username}")
    private String fromUsername;

    private final JavaMailSender mailSender;

    private void sendMail(String to, String title, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent(content, "text/html; charset=utf-8");
        message.setSubject(title);
        message.setFrom(fromUsername);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        log.info("Sending email to {}", to);
        mailSender.send(message);
        log.info("Email sent to {}", to);
    }

    public void sendMail(EmailSendingDto emailSendingDto) throws MessagingException {
        sendMail(emailSendingDto.getEmail(), emailSendingDto.getTitle(), emailSendingDto.getContent());
    }
}

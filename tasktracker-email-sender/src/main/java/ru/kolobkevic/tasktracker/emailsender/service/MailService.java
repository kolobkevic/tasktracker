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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.kolobkevic.tasktracker.emailsender.dto.UserTopicDto;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private static final String WELCOME_MAIL_TEMPLATE = "registration_email.html";
    private static final String WELCOME_MAIL_SUBJECT = "Welcome to TaskTracker";

    private final SpringTemplateEngine templateEngine;
        private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;


    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent(content, "text/html; charset=utf-8");
        message.setSubject(subject);
        message.setFrom(username);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        log.info("Sending email to {}", to);
        mailSender.send(message);
        log.info("Email sent to {}", to);
    }

    public void sendTemplateMail(String to, String subject, String template, Map<String, Object> variables)
            throws MessagingException {

        sendMail(to, subject, templateEngine.process(template, generateTemplateContext(variables)));
    }

    public void sendWelcomeMail(UserTopicDto user) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullName", user.getFirstname() + " " + user.getLastname());

        sendTemplateMail(user.getEmail(), WELCOME_MAIL_SUBJECT, WELCOME_MAIL_TEMPLATE, variables);
    }

    private Context generateTemplateContext(Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return context;
    }
}

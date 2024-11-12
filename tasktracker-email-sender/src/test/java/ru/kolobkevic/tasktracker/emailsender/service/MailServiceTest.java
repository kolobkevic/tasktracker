package ru.kolobkevic.tasktracker.emailsender.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import ru.kolobkevic.tasktracker.emailsender.dto.EmailSendingDto;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field fromUsernameField = MailService.class.getDeclaredField("fromUsername");
        fromUsernameField.setAccessible(true);
        fromUsernameField.set(mailService, "noreply@example.com");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendMail_ShouldSendEmailSuccessfully() throws MessagingException {
        String to = "user@example.com";
        String title = "Welcome!";
        String content = "<h1>Hello, user!</h1>";

        EmailSendingDto emailSendingDto = new EmailSendingDto(to, title, content);
        mailService.sendMail(emailSendingDto);

        verify(mimeMessage).setContent(content, "text/html; charset=utf-8");
        verify(mimeMessage).setSubject(title);
        verify(mimeMessage).setFrom("noreply@example.com");
        verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        verify(mailSender).send(mimeMessage);
    }
}

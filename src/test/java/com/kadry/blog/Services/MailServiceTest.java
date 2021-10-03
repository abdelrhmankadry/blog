package com.kadry.blog.Services;

import com.kadry.blog.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceTest {

    @Value("${spring.mail.username}")
    public String FROM_EMAIL;

    MailService mailService;

    @Spy
    JavaMailSenderImpl javaMailSender;

    @Captor
    ArgumentCaptor<MimeMessage> messageCaptor;


    SpringTemplateEngine templateEngine;

    @Before
    public void setUp() throws Exception {
        templateEngine = new SpringTemplateEngine();
        mailService  = new MailService(javaMailSender, templateEngine);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

    }

    @Test
    public void testSendActivationEmail() throws MessagingException, IOException {
        User user = createUser();
        mailService.sendActivationMail(user);
        assertCorrectArgumentsPassed("Account Activation");
    }

    @Test
    public void testSendResetPasswordEmail() throws IOException, MessagingException {
        User user = createUser();
        mailService.sendResetPasswordMail(user);
        assertCorrectArgumentsPassed("Reset Password");
    }

    private void assertCorrectArgumentsPassed(String subject) throws MessagingException, IOException {
        verify(javaMailSender).send(messageCaptor.capture());
        MimeMessage message = messageCaptor.getValue();
        assertEquals(subject, message.getSubject());
        assertEquals("test@domain.com", message.getAllRecipients()[0].toString());
        assertEquals(String.class, message.getContent().getClass());
        assertEquals("text/html;charset=UTF-8", message.getDataHandler().getContentType());
    }

    private User createUser() {
        User user = new User();
        user.setUsername("test_username");
        user.setPassword("test_password");
        user.setEmail("test@domain.com");
        user.setFirstName("test_firstname");
        user.setLastName("test_lastname");
        user.setActivationKey("test_activation_key");
        return user;
    }
}
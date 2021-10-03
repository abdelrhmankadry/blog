package com.kadry.blog.Services;

import com.kadry.blog.model.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class MailService {
    private final String BASE_URL = "http://localhost:8080";
    private final String URL = "url";

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }
    private void sendMailUsingTemplate(String subject, String email, Context context, String template){
        MimeMessage mimeMessage  = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    false, StandardCharsets.UTF_8.name());
            helper.setSubject(subject);
            helper.setTo(email);
            String content = templateEngine.process(template, context);
            helper.setText(content, true);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
    public void sendActivationMail(User user){
        final String SUBJECT = "Account Activation";
        final String ACTIVATION_MAIL_TEMPLATE = "activation_mail_template";
        Locale locale = Locale.ENGLISH;
        Context context = new Context(locale);
        String ACTIVATION_URL = BASE_URL + "/api/activate?key=";
        context.setVariable(URL, ACTIVATION_URL +user.getActivationKey());
        sendMailUsingTemplate(SUBJECT, user.getEmail(), context, ACTIVATION_MAIL_TEMPLATE);
    }

    public void sendResetPasswordMail(User user){
        final String SUBJECT = "Reset Password";
        final String RESET_PASSWORD_MAIL_TEMPLATE = "reset_password_template";
        Locale locale = Locale.ENGLISH;
        Context context = new Context(locale);
        String RESET_PASSWORD_URL = BASE_URL + "/api/account/reset-password/final?key=";
        context.setVariable(URL, RESET_PASSWORD_URL +user.getResetKey());
        sendMailUsingTemplate(SUBJECT, user.getEmail(), context, RESET_PASSWORD_MAIL_TEMPLATE);
    }
}

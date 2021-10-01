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
    public static final String ACTIVATION_URL = "http://localhost:8080/activate?key=";
    public static final String URL = "url";
    public static final String SUBJECT = "Account Activation";
    public static final String ACTIVATION_MAIL_TEMPLATE = "activation_mail_template";
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendActivationMail(User user){
        MimeMessage mimeMessage  = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    false, StandardCharsets.UTF_8.name());
            helper.setSubject(SUBJECT);
            helper.setTo(user.getEmail());
            Locale locale = Locale.ENGLISH;
            Context context = new Context(locale);
            context.setVariable(URL, ACTIVATION_URL +user.getActivationKey());
            String content = templateEngine.process(ACTIVATION_MAIL_TEMPLATE, context);
            helper.setText(content, true);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
}

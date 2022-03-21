package com.bida.employer.manager.notification;

import com.bida.employer.manager.domain.PasswordRecovery;
import com.bida.employer.manager.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;
    private MustacheFactory mf = new DefaultMustacheFactory();

    public void sendMessagePasswordRecoveryInitiate(User user, PasswordRecovery passwordRecovery) {
        HashMap<String, String> dataEmailTemplate = new HashMap<>();
        dataEmailTemplate.put("email", user.getEmail());
        // TODO add another fields to html email template
        try {
            sendHtmlMessage(user.getEmail(), "Password recovery", dataEmailTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHtmlMessage(String to, String subject, Map<String, String> templateData) throws MessagingException, IOException {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        Mustache m = mf.compile("templates/password_recovery.html");

        StringWriter writer = new StringWriter();
        m.execute(writer, templateData).flush();
        String html = writer.toString();

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(mail);
    }
}

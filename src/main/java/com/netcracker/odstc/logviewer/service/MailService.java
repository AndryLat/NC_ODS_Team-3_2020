package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String mailFrom;

    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, String token, User user) {
        String url = contextPath + "#/changePassword/" +
                user.getObjectId() + "/" + token;
        String message = "If you want to reset your account password on LogViewer, follow the link below: ";
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(mailFrom);
        return email;
    }
}

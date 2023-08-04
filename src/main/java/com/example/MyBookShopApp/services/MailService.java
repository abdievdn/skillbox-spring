package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.services.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${mail.ADDRESS}")
    private String mailFrom;

    private final JavaMailSender mailSender;

    public String sendMailSecretCode(String contact) {
        String generatedCode = CodeUtil.generateCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(contact);
        message.setText("Код для входа: " + generatedCode + "\n Время действия 5 минут.");
        message.setSubject("Bookstore CODE verification!");
        mailSender.send(message);
        return generatedCode;
    }
}

package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.services.util.CodeUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${twilio.AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${twilio.TWILIO_NUMBER}")
    private String TWILIO_NUMBER;

    public String sendSmsSecretCode(String contact) {
        String formattedContact = contact.replaceAll("[^+0-9]", "");
        log.info(formattedContact);
        String generatedCode = CodeUtil.generateCode();
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message.creator(new PhoneNumber(formattedContact),
//                new PhoneNumber(TWILIO_NUMBER),
//                "You secret code is: " + generatedCode).create();
        return generatedCode;
    }
}

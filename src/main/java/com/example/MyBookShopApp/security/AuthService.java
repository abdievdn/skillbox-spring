package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.enums.ContactType;
import com.example.MyBookShopApp.data.entity.user.UserContactEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.MailService;
import com.example.MyBookShopApp.services.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    public static final int CODE_EXPIRED_MIN = 5;
    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookShopUserService bookShopUserService;
    private final RegisterService registerService;
    private final SmsService smsService;
    private final MailService mailService;
    private final JWTUtil jwtUtil;

    public UserContactEntity getUserContact(String contact) {
        return userContactRepository.findByContact(contact).orElse(null);
    }

    @ServiceProcessTrackable
    public ResultDto checkSignupContact(ContactConfirmationDto confirmationDto) {
        UserContactEntity userContact = getUserContact(confirmationDto.getContact());
        if (isContactApproved(userContact)) {
            return new ResultDto("Такой контакт уже зарегистрирован!");
        } else {
            if (userContact == null) {
                userContact = UserContactEntity.builder()
                        .contact(confirmationDto.getContact())
                        .type(confirmationDto.getContactType().equals("phone") ?
                                ContactType.PHONE :
                                confirmationDto.getContactType().equals("mail")
                                        ? ContactType.MAIL : null)
                        .approved((short) 0)
                        .build();
                userContactRepository.save(userContact);
            }
            saveNewCode(userContact);
            return new ResultDto(true);
        }
    }

    private boolean isContactApproved(UserContactEntity userContact) {
        return userContact != null && userContact.getApproved() == 1;
    }

    @ServiceProcessTrackable
    public ResultDto checkSigninContact(ContactConfirmationDto confirmationDto) {
        UserContactEntity userContact = getUserContact(confirmationDto.getContact());
        if (!isContactApproved(userContact)) {
            return new ResultDto("Такого контакта не существует! Пройдите регистрацию.");
        }
        saveNewCode(userContact);
        return new ResultDto(true);
    }

    public ResultDto isSecretCodeValid(String contact, String code) {
        UserContactEntity userContact = getUserContact(contact);
        if (userContact != null && verifyCode(userContact, code)) {
            return new ResultDto(true);
        }
        return new ResultDto("Неверный код или истек срок его действия.");
    }

    private void saveNewCode(UserContactEntity contact) {
        String code;
        if (contact.getType().equals(ContactType.PHONE)) {
            code = smsService.sendSmsSecretCode(contact.getContact());
        } else if (contact.getType().equals(ContactType.MAIL)) {
            code = mailService.sendMailSecretCode(contact.getContact());
        } else {
            throw new UsernameNotFoundException("Не найден код!");
        }
        log.info(code);
        contact.setCode(passwordEncoder.encode(code));
        contact.setCodeTime(LocalDateTime.now().plusMinutes(CODE_EXPIRED_MIN));
        contact.setCodeTrails(contact.getCodeTrails() + 1);
        userContactRepository.save(contact);
    }

    private Boolean verifyCode(UserContactEntity userContact, String code) {
        return passwordEncoder.matches(code, userContact.getCode()) &&
                !userContact.isCodeExpired();
    }

    @ServiceProcessTrackable
    public void registerNewUser(UserDto userDto) {
        registerService.registerUser(
                userDto.getName(),
                userDto.getMail(),
                userDto.getPhone());
    }

    private ResultDto jwtLogin(ContactConfirmationDto payload) {
        int userId = getUserContact(payload.getContact()).getUser().getId();
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();
        userEntity.setPassword(passwordEncoder.encode(payload.getCode()));
        userRepository.save(userEntity);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, payload.getCode()));
        BookShopUser user = (BookShopUser) bookShopUserService
                .loadUserByUsername(String.valueOf(userId));
        return ResultDto.builder()
                .result(true)
                .value(jwtUtil.generateToken(user.getUsername()))
                .build();
    }

    @ServiceProcessTrackable
    public ResultDto login(ContactConfirmationDto payload, HttpServletResponse response) {
        if (!isPayloadCorrect(payload.getContact(), payload.getCode())) {
            return new ResultDto("Неверный ввод!");
        }
        ResultDto resultDto = jwtLogin(payload);
        Cookie cookie = new Cookie("token", resultDto.getValue());
        response.addCookie(cookie);
        return resultDto;
    }

    private boolean isPayloadCorrect(String contact, String code) {
        return !contact.isEmpty() && !code.isEmpty();
    }
}

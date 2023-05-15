package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.CurrentUserDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.struct.enums.ContactType;
import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.security.jwt.JWTAuthDto;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookShopUserDetailsService bookShopUserDetailsService;
    private final RegisterService registerService;
    private final JWTUtil jwtUtil;

    public ResultDto checkContact(ContactConfirmationDto confirmationDto) {
        if (userContactRepository.findByContact(confirmationDto.getContact()).isPresent()) {
            return new ResultDto("Такой контакт уже зарегистрирован!");
        } else {
            return new ResultDto(true);
        }
    }

    public void registerNewUser(AuthUserDto userDto) {
        registerService.registerUser(
                userDto.getName(),
                userDto.getEmail(),
                userDto.getPhone(),
                passwordEncoder.encode(userDto.getPassword()));
    }

    private JWTAuthDto jwtLogin(ContactConfirmationDto payload) {
        if (payload.getContact().isEmpty() || payload.getCode().isEmpty()) {
            return new JWTAuthDto("false", "Неверный ввод!");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        BookShopUserDetails userDetails =
                (BookShopUserDetails) bookShopUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
        return new JWTAuthDto(jwtToken);
    }

    public JWTAuthDto login(ContactConfirmationDto payload, HttpServletResponse response) {
        JWTAuthDto jwtAuthDto = jwtLogin(payload);
        Cookie cookie = new Cookie("token", jwtAuthDto.getResult());
        response.addCookie(cookie);
        return jwtAuthDto;
    }

    public CurrentUserDto getCurrentUser(Principal principal) {
        if (principal != null) {
            String name = "";
            String email = "";
            String phone = "";
            UserContactEntity userContactEntity = userContactRepository.findByContact(principal.getName()).orElseThrow();
            UserEntity user = userContactEntity.getUser();
            if (user != null) {
                name = user.getName();
                for (UserContactEntity contact : user.getContacts()) {
                    if (contact.getType().equals(ContactType.EMAIL)) {
                        email = contact.getContact();
                    } else {
                        phone = contact.getContact();
                    }
                }
            }
            return new CurrentUserDto(name, email, phone);
        } else {
            return null;
        }
    }
}

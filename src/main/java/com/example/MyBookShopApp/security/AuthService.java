package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.UserDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookShopUserService bookShopUserService;
    private final RegisterService registerService;
    private final JWTUtil jwtUtil;

    public ResultDto checkContact(ContactConfirmationDto confirmationDto) {
        if (userContactRepository.findByContact(confirmationDto.getContact()).isPresent()) {
            return new ResultDto("Такой контакт уже зарегистрирован!");
        } else {
            return new ResultDto(true);
        }
    }

    @ServiceProcessTrackable
    public void registerNewUser(UserDto userDto) {
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
        BookShopUser user =
                (BookShopUser) bookShopUserService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(user.getUsername());
        return new JWTAuthDto(jwtToken);
    }

    @ServiceProcessTrackable
    public JWTAuthDto login(ContactConfirmationDto payload, HttpServletResponse response) {
        JWTAuthDto jwtAuthDto = jwtLogin(payload);
        Cookie cookie = new Cookie("token", jwtAuthDto.getResult());
        response.addCookie(cookie);
        return jwtAuthDto;
    }
}

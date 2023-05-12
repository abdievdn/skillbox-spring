package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.security.AuthService;
import com.example.MyBookShopApp.security.AuthUserDto;
import com.example.MyBookShopApp.security.jwt.JWTAuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthService authService;

    @GetMapping("/signin")
    public String signinPage() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("regForm", new AuthUserDto());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ResultDto requestContactConfirmation(@RequestBody ContactConfirmationDto payload) {
        if (payload.getType()!= null && payload.getType().equals("signin")) {
            return new ResultDto(true);
        } else {
            return authService.checkContact(payload);
        }
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ResultDto approveContact(@RequestBody ContactConfirmationDto payload) {
        return new ResultDto(true);
    }

    @PostMapping("/registration")
    public String userRegistration(AuthUserDto authUserDto, Model model) {
        authService.registerNewUser(authUserDto);
        model.addAttribute("registrationOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public JWTAuthDto login(@RequestBody ContactConfirmationDto payload,
                            HttpServletResponse response) {
        JWTAuthDto jwtAuthDto = authService.jwtLogin(payload);
        Cookie cookie = new Cookie("token", jwtAuthDto.getResult());
        response.addCookie(cookie);
        return jwtAuthDto;
    }

    @GetMapping("/my")
    public String my() {
        return "my";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}

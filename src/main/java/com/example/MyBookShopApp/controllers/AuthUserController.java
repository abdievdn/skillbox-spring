package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.security.AuthService;
import com.example.MyBookShopApp.security.AuthUserDto;
import com.example.MyBookShopApp.security.jwt.JWTAuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
        if (payload.getType() != null && payload.getType().equals("signin")) {
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
        model.addAttribute("registrationOk", true);
        authService.registerNewUser(authUserDto);
        return "/signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public JWTAuthDto login(@RequestBody ContactConfirmationDto payload,
                            HttpServletResponse response) {
        return authService.login(payload, response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public String my() {
        return "my";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}

package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.security.RegistrationService;
import com.example.MyBookShopApp.security.RegistrationForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    @GetMapping("/signin")
    public String signinPage() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ResultDto requestContactConfirmation(@RequestBody ContactConfirmationDto payload) {
        if (payload.getType()!= null && payload.getType().equals("signin")) {
            return new ResultDto(true);
        } else {
            return registrationService.checkContact(payload);
        }
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ResultDto approveContact(@RequestBody ContactConfirmationDto payload) {
        return new ResultDto(true);
    }

    @PostMapping("/registration")
    public String userRegistration(RegistrationForm registrationForm, Model model) {
        registrationService.registerNewUser(registrationForm);
        model.addAttribute("registrationOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResultDto login(@RequestBody ContactConfirmationDto payload) {
        return registrationService.login(payload);
    }

    @GetMapping("/my")
    public String my() {
        return "my";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }
        return "redirect:/";
    }
}

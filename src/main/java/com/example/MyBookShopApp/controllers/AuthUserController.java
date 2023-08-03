package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.security.AuthService;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.security.jwt.JWTAuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
        model.addAttribute("regForm", new UserDto());
        return "signup";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ResultDto requestContactConfirmation(@RequestBody ContactConfirmationDto payload) {
        if (payload.getType() != null && payload.getType().equals("signin")) {
            return new ResultDto(true);
        } else {
            return authService.checkContact(payload);
        }
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/approveContact")
    @ResponseBody
    public ResultDto approveContact(@RequestBody ContactConfirmationDto payload) {
        return new ResultDto(true);
    }

    @PostMapping("/registration")
    public String userRegistration(UserDto userDto, Model model) {
        model.addAttribute("registrationOk", true);
        authService.registerNewUser(userDto);
        return "signin";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
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

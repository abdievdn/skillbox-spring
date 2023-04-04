package com.example.MyBookShopApp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }
}
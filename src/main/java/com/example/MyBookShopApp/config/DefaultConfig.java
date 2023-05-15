package com.example.MyBookShopApp.config;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DefaultConfig {
}

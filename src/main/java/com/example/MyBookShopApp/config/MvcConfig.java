package com.example.MyBookShopApp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(
        info = @Info(
                title = "BookShop",
                description = "This is platform where you can buy books and store its in your account",
                version = "0.1",
        license = @License(name = "OSL"),
        contact = @Contact(url = "http://localhost:8085", name = "Dmitrii A.", email = "adzmit@yandex.by")))
@Configuration
@EnableAsync
public class MvcConfig implements WebMvcConfigurer {

    @Value("${path.upload}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/book-covers/**").addResourceLocations("file:" + uploadPath + "/");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}

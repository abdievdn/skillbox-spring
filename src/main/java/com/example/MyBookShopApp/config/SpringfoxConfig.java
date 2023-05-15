package com.example.MyBookShopApp.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SpringfoxConfig {

    public final static String AUTHOR_TAG = "author-controller";
    public final static String BOOK_TAG = "book-controller";

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag(AUTHOR_TAG, "authors data api"),
                        new Tag(BOOK_TAG, "book data api"))
                .apiInfo(apiInfo());
    }

    public ApiInfo apiInfo() {
        return new ApiInfo(
                "Bookshop API",
                "API for Bookstore",
                "1.0",
                "http://www.termsofservice.org",
                new Contact("API owner", "http://www.ownersite.com", "owner@mail.com"),
                "api_license",
                "https://www.license.edu.org",
                new ArrayList<>()
        );
    }
}

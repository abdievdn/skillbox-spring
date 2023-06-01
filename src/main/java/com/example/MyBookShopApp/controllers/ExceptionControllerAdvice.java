package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.errors.CommonErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(CommonErrorException.class)
    public String handleCommonErrorException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchError", exception);
        return "redirect:/";
    }

//    @ExceptionHandler({NoSuchElementException.class, NullPointerException.class, IllegalStateException.class})
//    public String handleException(Exception ex) {
//        log.info(ex.getMessage());
//        return "redirect:/";
//    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleExpiredJwtException(Exception ex) {
        log.info(ex.getMessage());
        return "redirect:/signin";
    }
}

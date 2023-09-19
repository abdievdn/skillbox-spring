package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(CommonErrorException.class)
    public String handleCommonErrorException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchError", exception);
        return "redirect:/";
    }

    @ExceptionHandler(EntityNotFoundError.class)
    public ResultDto handleEntityNotFoundException(Exception exception) {
        return new ResultDto(exception.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class, NullPointerException.class, IllegalStateException.class})
    public void handleException(Exception ex, HttpServletResponse response) throws IOException {
        log.info(ex.getMessage());
        ex.printStackTrace();
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.sendRedirect("redirect:/signup");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public void handleExpiredJwtException(Exception ex, HttpServletResponse response) throws IOException {
        log.info(ex.getMessage());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.sendRedirect("redirect:/signup");
    }
}

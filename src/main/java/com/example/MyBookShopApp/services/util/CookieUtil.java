package com.example.MyBookShopApp.services.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void deleteCookieByName(HttpServletRequest request, HttpServletResponse response, String name) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(name)) {
                c.setPath("/");
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
    }

    public static void addCookieByName(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
        for (Cookie c : request.getCookies()) {
            c.setPath("/");
            c.setMaxAge(0);
            response.addCookie(c);
        }
    }
}

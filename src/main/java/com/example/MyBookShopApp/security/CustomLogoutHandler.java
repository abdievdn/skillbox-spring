package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.security.jwt.JWTBlacklistEntity;
import com.example.MyBookShopApp.security.jwt.JWTBlacklistRepository;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JWTBlacklistRepository blacklistRepository;
    private final UserContactRepository contactRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                UserContactEntity contact = contactRepository
                        .findByContact(jwtUtil.extractUsername(cookie.getValue()))
                        .orElse(null);
                JWTBlacklistEntity blacklistEntity = new JWTBlacklistEntity();
                blacklistEntity.setJwtValue(cookie.getValue());
                blacklistEntity.setUserContact(contact);
                blacklistRepository.save(blacklistEntity);
                if (contact != null) {
                    for (JWTBlacklistEntity jwt : contact.getJwtList()) {
                        deleteToken(jwt);
                    }
                }
            }
        }
    }

    private void deleteToken(JWTBlacklistEntity jwt) {
        try {
            jwtUtil.isTokenExpired(jwt.getJwtValue());
        } catch (ExpiredJwtException ex) {
            log.info(ex.getMessage());
            log.info("token id#" + jwt.getId() + " deleted from blacklist");
            blacklistRepository.delete(jwt);
        }
    }
}


package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                UserEntity userEntity = userRepository
                        .findById(Integer.valueOf(jwtUtil.extractUsername(cookie.getValue()))).orElseThrow();
                JWTBlacklistEntity blacklistEntity = new JWTBlacklistEntity();
                blacklistEntity.setJwtValue(cookie.getValue());
                blacklistEntity.setUser(userEntity);
                blacklistRepository.save(blacklistEntity);
                for (JWTBlacklistEntity jwt : userEntity.getJwtList()) {
                    deleteExpiredToken(jwt);
                }
            }
        }
    }

    private void deleteExpiredToken(JWTBlacklistEntity jwt) {
        try {
            jwtUtil.isTokenExpired(jwt.getJwtValue());
        } catch (ExpiredJwtException ex) {
            log.info(ex.getMessage());
            log.info("token id#" + jwt.getId() + " deleted from blacklist");
            blacklistRepository.delete(jwt);
        }
    }
}


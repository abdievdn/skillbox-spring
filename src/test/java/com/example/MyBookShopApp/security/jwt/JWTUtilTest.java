package com.example.MyBookShopApp.security.jwt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JWTUtilTest {

    private final JWTUtil jwtUtil;
    private final String username = "User";
    private final String token;

    @Autowired
    JWTUtilTest(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        token = jwtUtil.generateToken(username);
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void generateToken() {
        assertNotNull(token);
        assertTrue(token.contains("."));
        assertTrue(token.length() > 100);
    }

    @Test
    void extractUsername() {
        String usernameFromToken = jwtUtil.extractUsername(token);
        assertNotNull(usernameFromToken);
        assertEquals(usernameFromToken, username);
    }

    @Test
    void extractExpiration() {
        Date expirationDate = jwtUtil.extractExpiration(token);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void isTokenExpired() {
        Date expirationDate = jwtUtil.extractExpiration(token);
        assertNotNull(expirationDate);
        assertFalse(expirationDate.before(new Date()));
    }

    @Test
    void validateToken() {
        UserDetails userDetails = new User(username, "111111", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Boolean isValidToken = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValidToken);
    }
}
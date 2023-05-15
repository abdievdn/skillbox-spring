package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.BookShopUserDetails;
import com.example.MyBookShopApp.security.BookShopUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.IOException;
import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookShopUserDetailsService bookShopUserDetailsService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = request.getCookies();
        try {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        username = jwtUtil.extractUsername(token);
                    }
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        BookShopUserDetails userDetails =
                                (BookShopUserDetails) bookShopUserDetailsService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
        } catch (ExpiredJwtException ex) {
            log.info(SecurityContextHolder.getContext().toString());
            SecurityContextHolder.clearContext();
            for (Cookie c : cookies) {
                c.setMaxAge(0);
                c.setSecure(false);
                c.setHttpOnly(false);
                response.addCookie(c);
            }
            response.sendRedirect("/signin");
        }
        filterChain.doFilter(request, response);
    }
}

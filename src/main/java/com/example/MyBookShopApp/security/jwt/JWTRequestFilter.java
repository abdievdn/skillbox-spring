package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.BookShopUser;
import com.example.MyBookShopApp.security.BookShopUserService;
import com.example.MyBookShopApp.services.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
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
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookShopUserService bookShopUserService;
    private final JWTUtil jwtUtil;
    private final JWTBlacklistRepository blacklistRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
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
                        if (!blacklistRepository.existsByJwtValue(token)) {
                            BookShopUser userDetails =
                                    (BookShopUser) bookShopUserService.loadUserByUsername(username);
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
            }
        } catch (ExpiredJwtException ex) {
            SecurityContextHolder.clearContext();
            CookieUtil.deleteAllCookies(request, response);
            response.sendRedirect("/signin");
        }
        filterChain.doFilter(request, response);
    }
}

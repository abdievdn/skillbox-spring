package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import com.example.MyBookShopApp.security.oauth2.CustomOAuth2User;
import com.example.MyBookShopApp.security.oauth2.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final BookShopUserDetailsService bookShopUserDetailsService;
    private final CustomLogoutHandler logoutHandler;
    private final JWTRequestFilter filter;
    private final CustomOAuth2UserService oAuth2UserService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(bookShopUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile").authenticated()//.hasRole("USER")
                .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessUrl("/signin")
                .deleteCookies("token", "JSESSIONID", "cart", "postponed")
                .and()
                .oauth2Login()
                .loginPage("/signin")
                .successHandler((request, response, authentication) -> {
                    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                    oAuth2UserService.processOAuth2PostLogin(oAuth2User);
                    response.sendRedirect("/my");
                })
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

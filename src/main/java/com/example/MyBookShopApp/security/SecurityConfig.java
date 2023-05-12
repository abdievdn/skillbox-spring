package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig   {

    private final BookShopUserDetailsService bookShopUserDetailsService;
    private final JWTRequestFilter filter;

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
               .and().formLogin()
               .loginPage("/signin").failureUrl("/signin")
               .and().logout()
               .logoutUrl("/logout")
               .logoutSuccessUrl("/signin")
               .deleteCookies("token", "JSESSIONID", "cart", "postponed")
               .and().oauth2Login().defaultSuccessUrl("/my")
               .and().oauth2Client()
               .and().sessionManagement()
               .sessionCreationPolicy(SessionCreationPolicy.NEVER)
               .and()
               .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
               .build();
    }
}

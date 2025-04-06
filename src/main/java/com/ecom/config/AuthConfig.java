package com.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {
    private final AuthenticationProvider authenticationManagerBuilder;
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .authenticationProvider(authenticationManagerBuilder)
                   .build();
    }
}

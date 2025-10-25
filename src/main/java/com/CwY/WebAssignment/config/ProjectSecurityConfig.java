package com.CwY.WebAssignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(
                                        "/", "/index", "/about", "/course", "/instructor", "/instructor-details",
                                        "/blog", "/blog-single", "/contact", "/login", "/api/users/register",
                                        "/css/**", "/js/**", "/images/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                .loginPage("/login").permitAll())
                .logout(Customizer.withDefaults());

        return http.build();
    }
}
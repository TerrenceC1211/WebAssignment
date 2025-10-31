package com.CwY.WebAssignment.config;

import com.CwY.WebAssignment.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class ProjectSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService)
            throws Exception {
            http
                    .securityMatcher("/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/lecturer/**").hasRole("LECTURER")
                            .requestMatchers("/student/**").hasRole("STUDENT")
                            .requestMatchers(
                                    "/", "/index", "/about", "/course", "/instructor", "/instructor-details",
                                    "/blog", "/blog-single", "/contact", "/login", "/api/users/register",
                                    "/css/**", "/js/**", "/images/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                    )
                    .csrf(csrf -> csrf.disable())
                    .userDetailsService(userService)
                    .formLogin(form -> form
                            .loginPage("/login")
                            .successHandler((request, response, authentication) -> {
                                boolean isLecturer = authentication.getAuthorities().stream()
                                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_LECTURER"));

                                if (isLecturer) {
                                    response.sendRedirect("/lecturer/dashboard");
                                } else {
                                    response.sendRedirect("/student/dashboard");
                                }
                            })
                            .failureUrl("/login?error=true")
                            .permitAll())
                .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll());

            return http.build();
        }
    }

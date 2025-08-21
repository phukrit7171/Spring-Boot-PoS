package com.pos.phukrit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // --- FIX: Handle unauthorized API access ---
                // For unauthenticated users trying to access protected API endpoints,
                // return a 401 Unauthorized status code instead of redirecting to a login page.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible paths
                        .requestMatchers("/", "/index.html", "/app.js", "/favicon.ico").permitAll()
                        .requestMatchers("/h2-console/**", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                        // All other API requests must be authenticated
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                // --- FIX: Configure session management for the API ---
                // We add formLogin and httpBasic to ensure the security context is properly
                // created and managed after your custom login is successful.
                .formLogin(form -> form
                        .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
                        .failureHandler((req, res, ex) -> res.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )
                .httpBasic(httpBasic -> {}) // Enable HTTP Basic Auth for API clients
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );
        return http.build();
    }
}
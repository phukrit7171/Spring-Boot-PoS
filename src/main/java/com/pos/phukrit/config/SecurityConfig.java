package com.pos.phukrit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Allow specific origins - adjust these to match your frontend URLs
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "file://*"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        // Expose headers that the frontend might need
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "X-CSRF-TOKEN"
        ));

        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with the configuration above
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure CSRF - enable with cookie-based tokens for better frontend compatibility
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // Disable CSRF for API endpoints to simplify frontend integration
                        .ignoringRequestMatchers("/api/**", "/h2-console/**","/v3/api-docs/**","/swagger-ui/**")
                )

                // Handle unauthorized access properly
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible paths
                        .requestMatchers("/", "/index.html", "/app.js", "/favicon.ico", "/static/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**","/swagger-ui/**").permitAll()

                        // Allow anonymous order creation for self-checkout
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

                        // Allow GET requests to products for self-checkout browsing
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/search").permitAll()

                        // All other API requests require authentication
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )

                // Configure session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(10) // Allow multiple concurrent sessions
                        )
                )

                // Configure form login for web-based authentication
                .formLogin(form -> form
                        .loginPage("/login") // This won't be used with your custom endpoint
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType("application/json");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                        })
                )

                // Enable HTTP Basic Auth as fallback
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )

                // Configure headers
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // For H2 console
                        .contentTypeOptions(contentType -> {}) // Enable content type sniffing protection
                        .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable) // Disable HSTS for development
                )

                // IMPORTANT: Enable auto-save of SecurityContext to session
                // This ensures that when SecurityContextHolder.getContext().setAuthentication() 
                // is called in controllers, the authentication is automatically persisted to the session
                .securityContext(context -> context
                        .requireExplicitSave(false)   // auto-save SecurityContext changes to session
                );

        return http.build();
    }
}
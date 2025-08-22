package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.LoginReqDto;
import com.pos.phukrit.dtos.LoginResDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testSessionBasedAuthenticationFlow() {
        String baseUrl = "http://localhost:" + port;
        
        // Test 1: Access protected endpoint without authentication should return 401
        ResponseEntity<String> unauthenticatedResponse = restTemplate.getForEntity(
                baseUrl + "/api/users", String.class);
        assertThat(unauthenticatedResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // Test 2: Login with valid credentials
        LoginReqDto loginRequest = new LoginReqDto();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginReqDto> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

        ResponseEntity<LoginResDto> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login", 
                HttpMethod.POST, 
                loginEntity, 
                LoginResDto.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getUsername()).isEqualTo("admin");
        assertThat(loginResponse.getBody().getRole().toString()).isEqualTo("ADMIN");
        
        // Extract the session cookie (JSESSIONID)
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull();
        assertThat(cookies).isNotEmpty();
        
        String sessionCookie = cookies.stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No JSESSIONID cookie found"));

        // Test 3: Access protected endpoint with session cookie should return 200
        HttpHeaders authenticatedHeaders = new HttpHeaders();
        authenticatedHeaders.set(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<Void> authenticatedEntity = new HttpEntity<>(authenticatedHeaders);

        ResponseEntity<String> authenticatedResponse = restTemplate.exchange(
                baseUrl + "/api/users", 
                HttpMethod.GET, 
                authenticatedEntity, 
                String.class);

        assertThat(authenticatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authenticatedResponse.getBody()).contains("admin");
        assertThat(authenticatedResponse.getBody()).contains("ADMIN");
    }

    @Test
    public void testStaffUserCannotAccessAdminEndpoint() {
        String baseUrl = "http://localhost:" + port;
        
        // Login with staff credentials
        LoginReqDto loginRequest = new LoginReqDto();
        loginRequest.setUsername("staff");
        loginRequest.setPassword("password");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginReqDto> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

        ResponseEntity<LoginResDto> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login", 
                HttpMethod.POST, 
                loginEntity, 
                LoginResDto.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody().getUsername()).isEqualTo("staff");
        assertThat(loginResponse.getBody().getRole().toString()).isEqualTo("STAFF");

        // Extract the session cookie
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotNull().isNotEmpty();
        
        String sessionCookie = cookies.stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No JSESSIONID cookie found"));

        // Try to access admin-only endpoint - should return 403 Forbidden (not 401 Unauthorized)
        HttpHeaders authenticatedHeaders = new HttpHeaders();
        authenticatedHeaders.set(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<Void> authenticatedEntity = new HttpEntity<>(authenticatedHeaders);

        ResponseEntity<String> forbiddenResponse = restTemplate.exchange(
                baseUrl + "/api/users", 
                HttpMethod.GET, 
                authenticatedEntity, 
                String.class);

        // Should be 403 Forbidden (authenticated but not authorized), not 401 Unauthorized
        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testInvalidCredentialsReturnUnauthorized() {
        String baseUrl = "http://localhost:" + port;
        
        LoginReqDto loginRequest = new LoginReqDto();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrongpassword");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginReqDto> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login", 
                HttpMethod.POST, 
                loginEntity, 
                String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

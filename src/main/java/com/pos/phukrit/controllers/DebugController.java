package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.UserReqDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @PostMapping("/test-user-creation")
    public ResponseEntity<Map<String, Object>> testUserCreation(
            @RequestBody UserReqDto userReqDto,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Debug authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> authInfo = new HashMap<>();
        if (auth != null) {
            authInfo.put("isAuthenticated", auth.isAuthenticated());
            authInfo.put("principal", auth.getName());
            authInfo.put("authorities", auth.getAuthorities().toString());
        } else {
            authInfo.put("authentication", "null");
        }
        response.put("authentication", authInfo);
        
        // Debug session
        HttpSession session = request.getSession(false);
        Map<String, Object> sessionInfo = new HashMap<>();
        if (session != null) {
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("isNew", session.isNew());
            sessionInfo.put("creationTime", new Date(session.getCreationTime()));
            sessionInfo.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
        } else {
            sessionInfo.put("session", "null");
        }
        response.put("session", sessionInfo);
        
        // Debug headers
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        response.put("headers", headers);
        
        // Debug request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("method", request.getMethod());
        requestData.put("contentType", request.getContentType());
        requestData.put("remoteAddr", request.getRemoteAddr());
        requestData.put("userAgent", request.getHeader("User-Agent"));
        response.put("request", requestData);
        
        // Debug user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", userReqDto.getName());
        userData.put("username", userReqDto.getUsername());
        userData.put("email", userReqDto.getEmail());
        userData.put("role", userReqDto.getRole());
        userData.put("username_length", userReqDto.getUsername() != null ? userReqDto.getUsername().length() : "null");
        userData.put("username_bytes", userReqDto.getUsername() != null ? Arrays.toString(userReqDto.getUsername().getBytes()) : "null");
        response.put("userData", userData);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/session-info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Authentication info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> authInfo = new HashMap<>();
        if (auth != null) {
            authInfo.put("isAuthenticated", auth.isAuthenticated());
            authInfo.put("principal", auth.getName());
            authInfo.put("authorities", auth.getAuthorities().toString());
            authInfo.put("details", auth.getDetails().toString());
        } else {
            authInfo.put("authentication", "null");
        }
        response.put("authentication", authInfo);
        
        // Session info
        HttpSession session = request.getSession(false);
        Map<String, Object> sessionInfo = new HashMap<>();
        if (session != null) {
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("isNew", session.isNew());
            sessionInfo.put("creationTime", new Date(session.getCreationTime()));
            sessionInfo.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
            
            // List all session attributes
            Map<String, Object> attributes = new HashMap<>();
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attrName = attributeNames.nextElement();
                Object attrValue = session.getAttribute(attrName);
                attributes.put(attrName, attrValue.toString());
            }
            sessionInfo.put("attributes", attributes);
        } else {
            sessionInfo.put("session", "null");
        }
        response.put("session", sessionInfo);
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        response.put("headers", headers);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cors-test")
    public ResponseEntity<Map<String, Object>> corsTest(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS test endpoint - if you can see this, CORS is working");
        response.put("origin", request.getHeader("Origin"));
        response.put("method", request.getMethod());
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }
}

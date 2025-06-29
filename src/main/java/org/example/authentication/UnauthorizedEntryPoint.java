package org.example.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;
import java.io.Serializable;

@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
//        Object methodNotSupported = request.getAttribute("jakarta.servlet.error.exception");
//        System.out.println(methodNotSupported);
//        if (methodNotSupported instanceof HttpRequestMethodNotSupportedException) {
//            System.out.println("Method not allowed: {}" + request.getMethod());
//            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Method not allowed\"}");
//            return;
//        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthenticated\"}");

    }
}

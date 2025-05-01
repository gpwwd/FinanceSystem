package com.financialsystem.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "Anonymous";

        // Get request details
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();

        if (ex == null) {
            // Log successful request
            ActivityLogger.log(String.format("User '%s' successfully accessed '%s' [%s] - Status: %d",
                    username, endpoint, method, status));
        } else {
            // Log failed request with exception
            ActivityLogger.log(String.format("User '%s' encountered an error at '%s' [%s] - Status: %d - Exception: %s",
                    username, endpoint, method, status, ex.getMessage()));
        }
    }
}
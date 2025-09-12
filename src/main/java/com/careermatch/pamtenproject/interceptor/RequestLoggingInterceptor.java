package com.careermatch.pamtenproject.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);
        
        log.info("Request [{}] {} {} - User-Agent: {}", 
                requestId,
                request.getMethod(), 
                request.getRequestURI(),
                request.getHeader("User-Agent"));
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute("requestId");
        
        if (ex != null) {
            log.error("Request [{}] failed with status {} - Error: {}", 
                    requestId, response.getStatus(), ex.getMessage());
        } else {
            log.info("Request [{}] completed with status {}", 
                    requestId, response.getStatus());
        }
    }
} 
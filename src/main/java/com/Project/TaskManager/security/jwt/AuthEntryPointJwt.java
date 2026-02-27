package com.Project.TaskManager.security.jwt;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.Project.TaskManager.payload.response.ApiResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint{

        private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized request to : {} | Error: {}",
            request.getRequestURI(),
            authException.getMessage()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Void> apiResponse = ApiResponse.error(
            "Unauthorized: "+ authException.getMessage()
        );

       
        objectMapper.writeValue(response.getOutputStream(), apiResponse);

    }

    
    
}

package com.Project.TaskManager.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project.TaskManager.payload.request.LoginRequest;
import com.Project.TaskManager.payload.request.RegisterRequest;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.AuthResponse;
import com.Project.TaskManager.security.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) throws BadRequestException{
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User Registered Successfully",response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request
    ){

        AuthResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader("Refresh-Token") String refreshToken) {

        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity
                .ok(ApiResponse.success(
                        "Token refreshed successfully",
                        response
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Refresh-Token") String refreshToken) {

        authService.logout(refreshToken);
        return ResponseEntity
                .ok(ApiResponse.success("Logged out successfully"));
    }

    
}

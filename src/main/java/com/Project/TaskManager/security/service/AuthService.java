package com.Project.TaskManager.security.service;

import org.apache.coyote.BadRequestException;

import com.Project.TaskManager.payload.request.LoginRequest;
import com.Project.TaskManager.payload.request.RegisterRequest;
import com.Project.TaskManager.payload.response.AuthResponse;

public interface AuthService{

    AuthResponse register(RegisterRequest request) throws BadRequestException;

     AuthResponse login(LoginRequest request);

      AuthResponse refreshToken(String refreshToken);

      void logout(String refreshToken);


    
}

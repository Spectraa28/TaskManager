package com.Project.TaskManager.security.service;

import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.request.LoginRequest;
import com.Project.TaskManager.payload.request.RegisterRequest;
import com.Project.TaskManager.payload.response.AuthResponse;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.security.jwt.JwtUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils  jwtUtils;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) throws BadRequestException {
        //Email must not already Exist
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException(
                "Email Already in use" + request.getEmail()
            );

        }

        //Build and save the user
        User user = User.builder()
                        .fullName(request.getFullName()).
                        email(request.getEmail()).
                        password(passwordEncoder.encode(request.getPassword())).
                        build();

        User savedUser = userRepository.save(user);
        log.info("New User Registered: {}",savedUser.getEmail());

        //Generate Token
        String accessToken = jwtUtils
                            .generateTokenFromEmail(savedUser.getEmail());

        return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken("accessToken")
                            .tokenType("Bearer")
                            .userId(savedUser.getId())
                            .email(savedUser.getEmail())
                            .fullName(savedUser.getFullName())
                            .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
       // Authentivate via SPring Security
       Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
       );

       SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

       UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

       String accessToken = jwtUtils.generateTokenFromEmail(userDetails.getEmail());

       log.info("USer logged in: {} ", userDetails.getEmail());

        return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken("accessToken")
                            .tokenType("Bearer")
                            .userId(userDetails.getId())
                            .email(userDetails.getEmail())
                            .fullName(userDetails.getFullName())
                            .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }

    @Override
    public void logout(String refreshToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }


    
}

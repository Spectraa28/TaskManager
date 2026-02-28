package com.Project.TaskManager.security.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.model.RefreshToken;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.request.LoginRequest;
import com.Project.TaskManager.payload.request.RegisterRequest;
import com.Project.TaskManager.payload.response.AuthResponse;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.security.jwt.JwtUtils;
import com.Project.TaskManager.service.RefreshTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request){

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email already in use: " + request.getEmail()
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(
                        request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        String accessToken = jwtUtils
                .generateTokenFromEmail(savedUser.getEmail());

        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(savedUser);

        return buildAuthResponse(
                accessToken,
                refreshToken.getToken(),
                savedUser
        );
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository
                .findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new BadRequestException(
                        "User not found"
                ));

        String accessToken = jwtUtils
                .generateTokenFromEmail(userDetails.getEmail());

        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(user);

        log.info("User logged in: {}", userDetails.getEmail());

        return buildAuthResponse(
                accessToken,
                refreshToken.getToken(),
                user
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String token) {

        RefreshToken refreshToken = refreshTokenService
                .validaRefreshToken(token);

        User user = refreshToken.getUser();

        String newAccessToken = jwtUtils
                .generateTokenFromEmail(user.getEmail());

        // Rotate refresh token — generate new one
        RefreshToken newRefreshToken = refreshTokenService
                .createRefreshToken(user);

        log.info("Token refreshed for user: {}",
                user.getEmail());

        return buildAuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                user
        );
    }

    @Override
    @Transactional
    public void logout(String token) {

        refreshTokenService.revokeRefreshToken(token);
        log.info("User logged out via refresh token");
    }


    private AuthResponse buildAuthResponse(
            String accessToken,
            String refreshToken,
            User user) {

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
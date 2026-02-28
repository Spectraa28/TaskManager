package com.Project.TaskManager.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.model.RefreshToken;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiratoinMs;
   
   
    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
    
        //Delete  any existing token
        refreshTokenRepository.deleteByUser(user);

        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                                    .user(user)
                                    .token(UUID.randomUUID().toString())
                                    .expiresAt(Instant.now()
                                                       .plusMillis(refreshExpiratoinMs))
                                    .revoked(false)
                                    .build();
        
        RefreshToken savedrRefreshToken  =  refreshTokenRepository.save(refreshToken);

        log.info("Refresh token created for user : {}", user.getEmail());

        return savedrRefreshToken;
    
    }

    @Override
    @Transactional
    public RefreshToken validaRefreshToken(String token) throws BadRequestException {
    
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                                            .orElseThrow(()-> new ResourceNotFoundException(
                                                "Refresh token not found"));
        
        //check if revoked
        if(refreshToken.isRevoked()) {
            throw new BadRequestException(
                "Refresh token has been Revoked"
            );
        }

        //Check if expired
        if(refreshToken.getExpiresAt()
                        .isBefore(Instant.now())){
                    refreshTokenRepository.delete(refreshToken);
                    throw new BadRequestException("Refresh token has expired - please login again");
                        };

                        return refreshToken;
        
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
       RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Refresh token not found"
                ));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token revoked for user: {}",
                refreshToken.getUser().getEmail());
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("All refresh tokens revoked for user: {}",
                user.getEmail());
    }
    
    
}

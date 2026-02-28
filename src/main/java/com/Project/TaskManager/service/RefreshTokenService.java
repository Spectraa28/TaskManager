package com.Project.TaskManager.service;


import com.Project.TaskManager.model.RefreshToken;
import com.Project.TaskManager.model.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken validaRefreshToken(String token);

    void revokeRefreshToken(String token);
        
    void revokeAllUserTokens(User user);
}

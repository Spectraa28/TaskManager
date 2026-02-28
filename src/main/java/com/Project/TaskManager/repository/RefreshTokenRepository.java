package com.Project.TaskManager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.RefreshToken;
import com.Project.TaskManager.model.User;

@Repository
public interface RefreshTokenRepository 
                        extends  JpaRepository<RefreshToken,String>{
  Optional<RefreshToken> findByToken(String token);
  
  Optional<RefreshToken> findByUser(User user);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
  void deleteByUser(User user);

  boolean existsByTokenAndRevokedFalse(String token);
    
}

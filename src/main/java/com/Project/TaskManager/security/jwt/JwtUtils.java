package com.Project.TaskManager.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {
    

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public String generateTokenFromEmail(String email){
        return Jwts.builder()
                    .subject(email)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(getSigningKey())
                    .compact();
    }
     private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token){
        return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e){
            log.error("Invalid JWT token : {}",e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Expired Jwt TOken: {}", e.getMessage());
        }catch(UnsupportedJwtException e){
            log.error("Jwt token Unsupported : {} ",e.getMessage());
        }catch(IllegalArgumentException e){
            log.error("Jwt Claims String Empty: {}",e.getMessage());
        }
            return false;  
     }
}

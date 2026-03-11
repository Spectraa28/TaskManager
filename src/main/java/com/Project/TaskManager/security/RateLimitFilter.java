package com.Project.TaskManager.security;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends  OncePerRequestFilter {
    
    private final RedisTemplate<String ,Object > redisTemplate;

    private static final int MAX_REQUESTS = 10;

    private static final int WINDOW_MINUTES = 1;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only rate limit auth endpoints
        if (!isAuthEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String redisKey = RATE_LIMIT_PREFIX + clientIp + ":" + path;

        try {
            Long requestCount = redisTemplate.opsForValue().increment(redisKey);

            // First request — set expiry on the key
            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(redisKey, Duration.ofMinutes(WINDOW_MINUTES));
            }

            if (requestCount != null && requestCount > MAX_REQUESTS) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);
                sendRateLimitResponse(response);
                return;
            }

            // Add rate limit headers so client knows their limit status
            response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
            response.setHeader("X-RateLimit-Remaining",
                    String.valueOf(Math.max(0, MAX_REQUESTS - requestCount)));

        } catch (Exception e) {
            // If Redis is down — fail open (allow request)
            // Never let a cache failure block legitimate users
            log.error("Rate limit check failed — allowing request: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    // Helpers

    private boolean isAuthEndpoint(String path){
        return path.startsWith("/api/v1/auth");
    }

    private String getClientIp(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isEmpty()){
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

 private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {
                    "success": false,
                    "message": "Too many requests. Please wait 1 minute before trying again.",
                    "data": null
                }
                """);
    }
}

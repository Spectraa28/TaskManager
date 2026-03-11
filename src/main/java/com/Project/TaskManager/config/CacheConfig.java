package com.Project.TaskManager.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    // ─── Cache Names ──────────────────────────────────────────────────────────
    // Use these constants everywhere instead of raw strings
    // Prevents typos like "dashbord" vs "dashboard"

    // User dashboard cache — keyed by userId
    // TTL: 10 minutes (set in RedisConfig)
    public static final String USER_DASHBOARD_CACHE = "userDashboard";

    // Workspace dashboard cache — keyed by workspaceId
    // TTL: 10 minutes (set in RedisConfig)
    public static final String WORKSPACE_DASHBOARD_CACHE = "workspaceDashboard";

    // Search results cache — keyed by userId + keyword
    // TTL: 10 minutes (set in RedisConfig)
    public static final String SEARCH_CACHE = "search";
}
package com.Project.TaskManager.service;

import java.util.UUID;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.SearchResponse;

public interface SearchService {
    
        SearchResponse search(String keyword, UUID userId);
}

package com.Project.TaskManager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.SearchResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    
     private final SearchService searchService;

     @GetMapping
   public ResponseEntity<ApiResponse<SearchResponse>> search(
        @RequestParam(name = "q", defaultValue = "") String keyword,
        @AuthenticationPrincipal UserDetailsImpl currentUser) {
    SearchResponse response = searchService.search(keyword, currentUser.getId());
        // Guard — keyword too short
        if (keyword.trim().length() < 2) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Search keyword must be at least 2 characters"));
        }

        return ResponseEntity.ok(ApiResponse.success("Search completed", response));
    }
}

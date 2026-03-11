package com.Project.TaskManager.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.payload.response.DashboardResponse;
import com.Project.TaskManager.security.CurrentUser;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import com.Project.TaskManager.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;

    @GetMapping("/me")
public ResponseEntity<ApiResponse<DashboardResponse.UserDashboard>> getUserDashboard(
        @AuthenticationPrincipal UserDetailsImpl currentUser) {
    DashboardResponse.UserDashboard response =
            dashboardService.getUserDashboard(currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success(
            "Dashboard loaded successfully", response));
}

@GetMapping("/workspace/{workspaceId}")
public ResponseEntity<ApiResponse<DashboardResponse.WorkspaceDashboard>> getWorkspaceDashboard(
        @PathVariable UUID workspaceId,
        @AuthenticationPrincipal UserDetailsImpl currentUser) {
    DashboardResponse.WorkspaceDashboard response =
            dashboardService.getWorkspaceDashboard(workspaceId, currentUser.getId());
    return ResponseEntity.ok(ApiResponse.success(
            "Workspace dashboard loaded successfully", response));
}
}

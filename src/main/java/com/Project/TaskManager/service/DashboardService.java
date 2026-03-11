package com.Project.TaskManager.service;

import java.util.UUID;

import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.DashboardResponse;

public interface DashboardService {
   DashboardResponse.UserDashboard getUserDashboard(UUID userId);
DashboardResponse.WorkspaceDashboard getWorkspaceDashboard(UUID workspaceId, UUID currentUserId);
}

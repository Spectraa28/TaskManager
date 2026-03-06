package com.Project.TaskManager.service;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.model.Task;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.payload.response.ActivityLogResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;

public interface ActivityLogService {
    
    void log(Task task,
             User actor,
             String action,
             String  description,
             String oldValue,
             String newValue
    );

    void log(Task task,
             User actor,
             String action,
             String description
    );

    Page<ActivityLogResponse> getTaskActivity(UserDetailsImpl currentUser,
                                              UUID taskId,
                                              Pageable pageable
    );

    Page<ActivityLogResponse> getProjectActivity(UserDetailsImpl currentUser,
                                                 UUID projectId,
                                                 Pageable pageable
    );
}

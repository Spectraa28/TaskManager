package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Project.TaskManager.payload.request.CreateSprintRequest;
import com.Project.TaskManager.payload.response.SprintResponse;
import com.Project.TaskManager.security.service.UserDetailsImpl;

public interface SprintService {
    

    // crEate a new Sprint inside a project
    SprintResponse createSprint(UserDetailsImpl currentUser, UUID projectid, CreateSprintRequest request);

    //List all sprint in a project
    Page<SprintResponse> getSprintByProject(UserDetailsImpl currentUser, UUID projectid, Pageable pageable);

    //Update sprint details 
    SprintResponse updateSprint(UserDetailsImpl currentUser, UUID projectid,UUID sprintId, CreateSprintRequest request);

    //Transition sprint froM Planned to Active
    SprintResponse startSprint(UserDetailsImpl currentUser, UUID projectid,UUID sprintId);  

    // Activve to complete
    SprintResponse completeSprint(UserDetailsImpl currentUser, UUID projectid,UUID sprintId);
}

package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.enums.SprintStatus;
import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.Sprint;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
import com.Project.TaskManager.payload.request.CreateSprintRequest;
import com.Project.TaskManager.payload.response.SprintResponse;
import com.Project.TaskManager.repository.ProjectRepository;
import com.Project.TaskManager.repository.SprintRepository;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.repository.WorkspaceMemberRepository;
import com.Project.TaskManager.repository.WorkspaceRepository;
import com.Project.TaskManager.security.RequiresWorkspaceRole;
import com.Project.TaskManager.security.service.UserDetailsImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService{
    
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository  workspaceMemberRepository;
    private final UserRepository userRepository;

    // Create Sprint 
    @RequiresWorkspaceRole(WorkspaceRole.MANAGER) 
    @Override
    @Transactional
    public SprintResponse createSprint(UserDetailsImpl currentUser, UUID projectId, CreateSprintRequest request){

        Project project = getProjectById(projectId);


        if(!request.getEndDate().isAfter(request.getStartDate())){
            throw new BadRequestException("End date must be after start date");
        }

        Sprint  sprint = Sprint.builder()
                    .name(request.getName())
                    .goal(request.getGoal())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .status(SprintStatus.PLANNED)
                    .project(project)
                    .build();

                    Sprint saved = sprintRepository.save(sprint);

                    log.info("Sprint '{}' created in project '{}' by '{}'",
                        saved.getName(),project.getKey(),currentUser.getEmail()
                    );

        return SprintResponse.from(saved);
    }

    //Read    

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER)
    @Override
    @Transactional
    public Page<SprintResponse> getSprintByProject(UserDetailsImpl currentUser, UUID projectid, Pageable pageable) {
            Project project = getProjectById(projectid);


            return sprintRepository.findAllByProject(project, pageable).map(SprintResponse::from);
    }


    // Update
    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)
    @Override
    @Transactional
    public SprintResponse updateSprint(UserDetailsImpl currentUser, UUID projectid, UUID sprintId,
            CreateSprintRequest request) {
    
                Project project = getProjectById(projectid);


                Sprint sprint = getSprintByIdAndProject(sprintId, project);

                if(sprint.getStatus() != SprintStatus.PLANNED){
                    throw new BadRequestException("Only Planned Sprints can be Updated. " + "Current Status: "+ sprint.getStartDate() );
                }

                if(!request.getEndDate().isAfter(request.getStartDate())){
                    throw new BadRequestException("ENd date must be after start date");
                }

                sprint.setName(request.getName());
                sprint.setGoal(request.getGoal());
                sprint.setStartDate(request.getStartDate());
                sprint.setEndDate(request.getEndDate());

                Sprint updated = sprintRepository.save(sprint);

                log.info("Sprint '{}' updated by '{}'", updated.getName(), currentUser.getEmail());

                return SprintResponse.from(updated);
            }




    // Start sprint _________
    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)        
    @Override
    @Transactional
    public SprintResponse startSprint(UserDetailsImpl currentUser, UUID projectid, UUID sprintId) {
    Project project = getProjectById(projectid);


        Sprint sprint = getSprintByIdAndProject(sprintId, project);

        // Must be PLANNED to start
        if (sprint.getStatus() != SprintStatus.PLANNED) {
            throw new BadRequestException(
                    "Only PLANNED sprints can be started. " +
                    "Current status: " + sprint.getStatus());
        }

        // Only one ACTIVE sprint per project at a time
        if (sprintRepository.existsByProjectAndStatus(
                project, SprintStatus.ACTIVE)) {
            throw new BadRequestException(
                    "Project already has an active sprint. " +
                    "Complete it before starting a new one");
        }

        sprint.setStatus(SprintStatus.ACTIVE);
        Sprint started = sprintRepository.save(sprint);

        log.info("Sprint '{}' started in project '{}' by '{}'",
                started.getName(), project.getKey(), currentUser.getEmail());

        return SprintResponse.from(started);
    }

    // Complete sprint

    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)
    @Override
    @Transactional
    public SprintResponse completeSprint(UserDetailsImpl currentUser, UUID projectid, UUID sprintId) {
      Project project = getProjectById(projectid);


        Sprint sprint = getSprintByIdAndProject(sprintId, project);

        // Must be ACTIVE to complete
        if (sprint.getStatus() != SprintStatus.ACTIVE) {
            throw new BadRequestException(
                    "Only ACTIVE sprints can be completed. " +
                    "Current status: " + sprint.getStatus());
        }

        sprint.setStatus(SprintStatus.COMPLETED);
        Sprint completed = sprintRepository.save(sprint);

        log.info("Sprint '{}' completed in project '{}' by '{}'",
                completed.getName(), project.getKey(), currentUser.getEmail());

        return SprintResponse.from(completed); }

    // helpers
    private Project getProjectById(UUID projectId){
        return projectRepository.findById(projectId).
                    orElseThrow(()-> new ResourceNotFoundException("Project not found: " + projectId));
    }

    private Sprint getSprintByIdAndProject(UUID sprintId  , Project project){
        Sprint sprint = sprintRepository.findById(sprintId)
                        .orElseThrow(()-> new ResourceNotFoundException("Sprint not found: "+ sprintId));

        if(!sprint.getProject().getId().equals(project.getId())){
            throw new ResourceNotFoundException("Sprint not found in this project");

        }

        return sprint;
    
    }
    
    private User getUserById(UUID userId){
            return userRepository.findById(userId)
                                .orElseThrow(()-> new ResourceNotFoundException("User not found: "+ userId));
        
    }

    
}

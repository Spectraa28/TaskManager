package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Project.TaskManager.enums.TaskStatus;
import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.Project;
import com.Project.TaskManager.model.ProjectMember;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
import com.Project.TaskManager.payload.request.CreateProjectRequest;
import com.Project.TaskManager.payload.response.ProjectResponse;
import com.Project.TaskManager.repository.ProjectMemberRepository;
import com.Project.TaskManager.repository.ProjectRepository;
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
public class ProjectServiceImpl implements ProjectService{
    
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;


    // Create PRoject

    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)
    @Override
    @Transactional
    public ProjectResponse createProject(UserDetailsImpl currentUser,
                            UUID workspaceId,
                            CreateProjectRequest request
    ){
        Workspace workspace = getWorkspaceById(workspaceId);

    
        String key = request.getKey().toUpperCase();

        if(projectRepository.existsByWorkspaceAndKey(workspace, key)){
            throw new BadRequestException("Project key : " + key + " Already iin tHis workspace");
        }


        User creator = getUserById(currentUser.getId());

        Project project = Project.builder()
                                .name(request.getName())
                                .key(key)
                                .description(request.getDescription())
                                .workspace(workspace)
                                .projectLead(creator)
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .status(TaskStatus.TODO)
                                .archived(false)
                                .build();

        Project saved = projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                                        .project(saved)
                                        .user(creator)
                                        .role(WorkspaceRole.ADMIN)
                                        .build();
        
        projectMemberRepository.save(member);

        log.info("Project '{}'  created in workspace '{}' by '{}'",
                        saved.getKey() , workspace.getSlug() , creator.getEmail()
        );


        return ProjectResponse.from(saved);
    }

    //_________________ ReaD ________________---

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER)
    @Override
    @Transactional
    public Page<ProjectResponse> getProjectsByWorkspace(UserDetailsImpl currentUser,UUID workspaceId, Pageable pageable){
        Workspace workspace = getWorkspaceById(workspaceId);


        return projectRepository.findAllByWorkspace(workspace, pageable)
                                .map(ProjectResponse::from);
    }

    @RequiresWorkspaceRole(WorkspaceRole.DEVELOPER)
    @Override
    @Transactional
    public ProjectResponse getProjectById(UserDetailsImpl currentUser,UUID workspaceId, UUID projectId){
        Workspace workspace = getWorkspaceById(workspaceId);

        Project project = getProjectByIdAndWorkspace(projectId, workspace);

        return  ProjectResponse.from(project);
    }

    // ─── Update ─────────────────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)
    @Override
    @Transactional
    public ProjectResponse updateProject(UserDetailsImpl currentUser,
                                         UUID workspaceId,
                                         UUID projectId,
                                         CreateProjectRequest request) {

        Workspace workspace = getWorkspaceById(workspaceId);

        Project project = getProjectByIdAndWorkspace(projectId, workspace);

        // If key is being changed — check new key is not taken
        String newKey = request.getKey().toUpperCase();
        if (!newKey.equals(project.getKey()) &&
                projectRepository.existsByWorkspaceAndKey(workspace, newKey)) {
            throw new BadRequestException(
                    "Project key '" + newKey + "' already exists in this workspace");
        }

        project.setName(request.getName());
        project.setKey(newKey);
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        Project updated = projectRepository.save(project);

        log.info("Project '{}' updated by '{}'",
                updated.getKey(), currentUser.getEmail());

        return ProjectResponse.from(updated);
    }

    // ─── Archive ────────────────────────────────────────────────────────────

    @RequiresWorkspaceRole(WorkspaceRole.MANAGER)
    @Override
    @Transactional
    public void archiveProject(UserDetailsImpl currentUser,
                               UUID workspaceId,
                               UUID projectId) {

        Workspace workspace = getWorkspaceById(workspaceId);

        Project project = getProjectByIdAndWorkspace(projectId, workspace);

        project.setArchived(true);
        projectRepository.save(project);

        log.info("Project '{}' archived by '{}'",
                project.getKey(), currentUser.getEmail());
    }







 // ---------------- helperS ---------------
    private Workspace getWorkspaceById(UUID workspaceId){
        return workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new ResourceNotFoundException("Workspace not FOund: " + workspaceId)
        );
    }


    private Project getProjectByIdAndWorkspace(UUID projectId, Workspace workspace){
        Project project = projectRepository.findById(projectId)
                            .orElseThrow(()-> new ResourceNotFoundException(
                                "Project Not FOund: " + projectId
                            ));

        if(!project.getWorkspace().getId().equals(workspace.getId())){
            throw new ResourceNotFoundException(
                "Project not found in this workspace"
            );
        }
        if(project.isArchived()){
            throw new 
                 ResourceNotFoundException
                   ("Project not found in this workspace");
        
        }

        return project;

        }

        private User getUserById(UUID userid){
            return userRepository.findById(userid)
                    .orElseThrow(()->
                            new ResourceNotFoundException("User not found: "+ userid));   
        }
    
       }

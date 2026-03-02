package com.Project.TaskManager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
import com.Project.TaskManager.payload.request.CreateWorkspaceRequest;
import com.Project.TaskManager.payload.request.InviteMemberRequest;
import com.Project.TaskManager.payload.response.WorkspaceResponse;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.repository.WorkspaceMemberRepository;
import com.Project.TaskManager.repository.WorkspaceRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService{
    
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    
    
    
    @Override
    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, UUID userId) {
        
        //validate slug is not taken 
        if(workspaceRepository.existsBySlug(request.getSlug())){
            throw new BadRequestException("Slug is already in use" + request.getSlug());
        }

        //Load the user
        User owner = userRepository.findById(userId).
                    orElseThrow(()-> new ResourceNotFoundException("User not found"));
    
    

        //Build and save workspace
        Workspace workspace = Workspace.builder()
                                        .name(request.getName())
                                        .description(request.getDescription())
                                        .slug(request.getSlug())
                                        .owner(owner)
                                        .archived(false)
                                        .build();
        Workspace saved = workspaceRepository.save(workspace);

        // Auto add creater as ADmin member
        WorkspaceMember member = WorkspaceMember.builder()
                                            .workspace(workspace)
                                            .user(owner)
                                            .role(WorkspaceRole.ADMIN)
                                            .build();

        workspaceMemberRepository.save(member);

        log.info("Workspace created : {} by user: {}"
            , saved.getSlug(), owner.getEmail()
        );

        return mapToResponse(saved, WorkspaceRole.ADMIN,1);
}

    @Override
    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceBySlug(String slug, UUID userId) {
        Workspace workspace = workspaceRepository
                            .findBySlug(slug)
                            .orElseThrow(()-> new ResourceNotFoundException("Workspace not found: "+ slug));
        
        User user = userRepository.findById(userId)
                                    .orElseThrow(()-> new ResourceNotFoundException("User not Found :"));

        //check Membership
        WorkspaceMember member = workspaceMemberRepository
                                    .findByWorkspaceAndUser(workspace, user)
                                    .orElseThrow(()-> new UnauthorizedException("YOu are not a member of this workspace"));

    
       int memberCount = workspaceMemberRepository
                .countByWorkspace(workspace);

        return mapToResponse(workspace, member.getRole(), memberCount);
    }

    @Override
    @Transactional
    public WorkspaceResponse updatedWorkspace(UUID workspaceId, CreateWorkspaceRequest request, UUID userId) {
     Workspace workspace = workspaceRepository
                                        .findById(workspaceId)
                                        .orElseThrow(()->new ResourceNotFoundException("Workspace not found"));
    
     User user = userRepository.findById(userId).orElseThrow(()->
                             new ResourceNotFoundException("User not found"));
    //ONly Admin can update                    
    validateAdminRole(workspace, user);


    //  check slug not taken by another workspace
    if(!workspace.getSlug().equals(request.getSlug()) && workspaceRepository.existsBySlug(request.getSlug())){
        throw new BadRequestException(
            "Slug already in use : " + request.getSlug()
        );
    }

    workspace.setName(request.getName());
    workspace.setDescription(request.getDescription());
    workspace.setSlug(request.getSlug());

    Workspace updated  = workspaceRepository.save(workspace);

    int memberCount = workspaceMemberRepository.countByWorkspace(updated);

    log.info("Workspace updated: {}", updated.getSlug());

    return mapToResponse(updated, WorkspaceRole.ADMIN, memberCount);
    }


     @Override
     @Transactional
    public void deleteWorkspace(UUID workspaceId, UUID userId) {
    
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
            ()-> new ResourceNotFoundException("Workspace not found")
        );

        User user = userRepository.findById(userId).orElseThrow(
            ()-> new ResourceNotFoundException("User not found")
        );

        //Only Admin can delete
        validateAdminRole(workspace, user);

        //Soft delete 
        workspace.setArchived(true);
        workspaceRepository.save(workspace);

        log.info("Workspace archived: {} by user : {}",
                    workspace.getSlug(), user.getEmail()
                        );  
    
    }
    
    
    
    
    @Override
    @Transactional(readOnly = true)
    public Page<WorkspaceResponse> getMyWorkspaces(UUID userId, Pageable pageable) {
    
      User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        return workspaceRepository
                .findAllByMemberOrOwner(user, pageable)
                .map(workspace -> {
                    WorkspaceMember member =
                            workspaceMemberRepository
                                    .findByWorkspaceAndUser(
                                            workspace, user)
                                    .orElse(null);

                    WorkspaceRole role = member != null
                            ? member.getRole()
                            : WorkspaceRole.ADMIN;

                    int count = workspaceMemberRepository
                            .countByWorkspace(workspace);

                    return mapToResponse(workspace, role, count);
                });
    }



    @Override
    @Transactional
    public WorkspaceResponse inviteMember(UUID workspaceId, InviteMemberRequest request, UUID userID) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
            ()-> new ResourceNotFoundException("Workspace not found")
        );

        User inviter = userRepository.findById(userID).orElseThrow(
            ()-> new ResourceNotFoundException("user not Found")
        );

        validateAdminRole(workspace, inviter);

        User invitee = userRepository.findByEmail(request.getEmail()).orElseThrow(
            ()-> new ResourceNotFoundException("User not found with email: " + request.getEmail())
        );

        if(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, invitee)){
            throw new BadRequestException("User is already a member in this workspace");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                                                .workspace(workspace)
                                                .user(invitee)
                                                .role(request.getRole())
                                                .build();

        workspaceMemberRepository.save(member);

        log.info("User {} inted to workspace {} with role {}",
            invitee.getEmail(),
            workspace.getSlug(),
            request.getRole()
        );

        int memberCount = workspaceMemberRepository.countByWorkspace(workspace);

        return mapToResponse(workspace, WorkspaceRole.ADMIN, memberCount);

    }




    @Override
    @Transactional
    public void removeMember(UUID workspaceId, UUID memeberId, UUID userID) {
    Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
            ()-> new ResourceNotFoundException("Workspace not found")
        );

        User requester = userRepository.findById(userID).orElseThrow(
            ()-> new ResourceNotFoundException("user not Found")
        );

        validateAdminRole(workspace, requester);

        User memberToRemove = userRepository.findById(memeberId).orElseThrow(
            ()-> new ResourceNotFoundException("User not found")
        );

        if(workspace.getOwner().getId().equals(memberToRemove)){
            throw new BadRequestException("Cannot remove the workspace owner");
        }

        workspaceMemberRepository.deleteByWorkspaceAndUser(workspace, memberToRemove);
        
        log.info("USer {} removed from workspace {} by {}", memberToRemove.getEmail(),
    workspace.getSlug(),
requester.getEmail());
    
    }




    private void validateAdminRole(Workspace workspace, User user) {
        boolean isAdmin = workspaceMemberRepository
                .existsByWorkspaceAndUserAndRole(
                        workspace, user, WorkspaceRole.ADMIN
                );

        if (!isAdmin) {
            throw new UnauthorizedException(
                    "Only ADMINs can perform this action"
            );
        }
    }

    private WorkspaceResponse mapToResponse(
            Workspace workspace,
            WorkspaceRole currentUserRole,
            int memberCount) {

        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .slug(workspace.getSlug())
                .archived(workspace.isArchived())
                .ownerId(workspace.getOwner().getId())
                .ownerName(workspace.getOwner().getFullName())
                .ownerEmail(workspace.getOwner().getEmail())
                .currentUserRole(currentUserRole)
                .memberCount(memberCount)
                .createdAt(workspace.getCreatedAt())
                .updatedAt(workspace.getUpdatedAt())
                .build();
    }



}

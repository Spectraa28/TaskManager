package com.Project.TaskManager.security;

import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
import com.Project.TaskManager.repository.ProjectRepository;
import com.Project.TaskManager.repository.UserRepository;
import com.Project.TaskManager.repository.WorkspaceMemberRepository;
import com.Project.TaskManager.repository.WorkspaceRepository;
import com.Project.TaskManager.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WorkspaceRoleAspect {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Around("@annotation(com.Project.TaskManager.security.RequiresWorkspaceRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresWorkspaceRole annotation = method
                .getAnnotation(RequiresWorkspaceRole.class);
        WorkspaceRole requiredRole = annotation.value();

        Object[] args = joinPoint.getArgs();

        // Extract UserDetailsImpl and first UUID from args
        UserDetailsImpl[] currentUserHolder = {null};
        UUID[] uuidHolder = {null};

        for (Object arg : args) {
            if (arg instanceof UserDetailsImpl userDetails) {
                currentUserHolder[0] = userDetails;
            }
            if (arg instanceof UUID uuid && uuidHolder[0] == null) {
                uuidHolder[0] = uuid;
            }
        }

        UserDetailsImpl currentUser = currentUserHolder[0];
        UUID firstUUID = uuidHolder[0];

        // Skip check if we can't find required args
        if (currentUser == null || firstUUID == null) {
            log.warn("@RequiresWorkspaceRole could not find " +
                    "UserDetailsImpl or UUID in method args — skipping check");
            return joinPoint.proceed();
        }

        // Resolve workspace — first UUID could be workspaceId or projectId
        Workspace workspace = null;

        var directWorkspace = workspaceRepository.findById(firstUUID);
        if (directWorkspace.isPresent()) {
            workspace = directWorkspace.get();
        } else {
            var project = projectRepository.findById(firstUUID);
            if (project.isPresent()) {
                workspace = project.get().getWorkspace();
            }
        }

        // Skip check if workspace can't be resolved
        if (workspace == null) {
            log.warn("@RequiresWorkspaceRole could not resolve " +
                    "workspace from UUID '{}' — skipping check", firstUUID);
            return joinPoint.proceed();
        }

        // Workspace owner always passes
        if (workspace.getOwner().getId().equals(currentUser.getId())) {
            return joinPoint.proceed();
        }

        // Load user
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + currentUser.getId()));

        // Find membership record
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, user)
                .orElseThrow(() -> new UnauthorizedException(
                        "You are not a member of this workspace"));

        // Check role hierarchy — ADMIN > MANAGER > DEVELOPER
        if (!hasRequiredRole(member.getRole(), requiredRole)) {
            throw new UnauthorizedException(
                    "Required role: " + requiredRole +
                    ". Your role: " + member.getRole());
        }

        log.debug("Role check passed for user '{}' with role '{}' " +
                "on workspace '{}'",
                currentUser.getEmail(), member.getRole(), firstUUID);

        return joinPoint.proceed();
    }

    private boolean hasRequiredRole(WorkspaceRole actual,
                                    WorkspaceRole required) {
        return switch (required) {
            case DEVELOPER -> true;
            case MANAGER -> actual == WorkspaceRole.MANAGER
                    || actual == WorkspaceRole.ADMIN;
            case ADMIN -> actual == WorkspaceRole.ADMIN;
        };
    }
}
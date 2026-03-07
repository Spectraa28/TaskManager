package com.Project.TaskManager.security;

import com.Project.TaskManager.enums.WorkspaceRole;
import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.exceptions.UnauthorizedException;
import com.Project.TaskManager.model.User;
import com.Project.TaskManager.model.Workspace;
import com.Project.TaskManager.model.WorkspaceMember;
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

    @Around("@annotation(com.Project.TaskManager.security.RequiresWorkspaceRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {

        // Get the annotation from the intercepted method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresWorkspaceRole annotation = method
                .getAnnotation(RequiresWorkspaceRole.class);
        WorkspaceRole requiredRole = annotation.value();

        // Extract method arguments
        Object[] args = joinPoint.getArgs();

        // Find UserDetailsImpl and workspaceId from method arguments
       UserDetailsImpl[] currentUserHolder = {null};
UUID[] workspaceIdHolder = {null};

for (Object arg : args) {
    if (arg instanceof UserDetailsImpl userDetails) {
        currentUserHolder[0] = userDetails;
    }
    if (arg instanceof UUID uuid && workspaceIdHolder[0] == null) {
        workspaceIdHolder[0] = uuid;
    }
}

UserDetailsImpl currentUser = currentUserHolder[0];
UUID workspaceId = workspaceIdHolder[0];

        // If we can't find the user or workspaceId — skip the check
        if (currentUser == null || workspaceId == null) {
            log.warn("@RequiresWorkspaceRole could not find " +
                    "UserDetailsImpl or workspaceId in method args — skipping check");
            return joinPoint.proceed();
        }

        // Load workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workspace not found: " + workspaceId));

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
                currentUser.getEmail(), member.getRole(), workspaceId);

        return joinPoint.proceed();
    }

    // Role hierarchy: ADMIN > MANAGER > DEVELOPER
    private boolean hasRequiredRole(WorkspaceRole actual,
                                    WorkspaceRole required) {
        return switch (required) {
            case DEVELOPER -> true; // any member passes
            case MANAGER -> actual == WorkspaceRole.MANAGER
                    || actual == WorkspaceRole.ADMIN;
            case ADMIN -> actual == WorkspaceRole.ADMIN;
        };
    }
}
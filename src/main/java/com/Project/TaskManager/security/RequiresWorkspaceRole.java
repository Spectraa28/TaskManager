package com.Project.TaskManager.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.Project.TaskManager.enums.WorkspaceRole;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresWorkspaceRole {
    WorkspaceRole value() default WorkspaceRole.DEVELOPER;
}

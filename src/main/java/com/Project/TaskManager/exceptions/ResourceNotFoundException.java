package com.Project.TaskManager.exceptions;

public class ResourceNotFoundException extends RuntimeException{
     public ResourceNotFoundException (String message){
        super(message);
    }
}

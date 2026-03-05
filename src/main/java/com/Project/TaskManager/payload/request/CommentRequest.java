package com.Project.TaskManager.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(min=1,max = 2000, message = "COmment must be between 1 and 2000 characters")
    private String content;
}

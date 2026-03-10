package com.Project.TaskManager.service;

import com.Project.TaskManager.payload.request.AiRequest;
import com.Project.TaskManager.payload.response.AiResponse;

public interface GeminiService {
    
    AiResponse generateTaskDescription(AiRequest request);

    AiResponse generateSprintSummary(AiRequest request);

    AiResponse suggestTaskPriority(AiRequest  request);
}

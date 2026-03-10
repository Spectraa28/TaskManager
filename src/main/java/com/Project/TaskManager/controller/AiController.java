package com.Project.TaskManager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project.TaskManager.payload.request.AiRequest;
import com.Project.TaskManager.payload.response.AiResponse;
import com.Project.TaskManager.payload.response.ApiResponse;
import com.Project.TaskManager.service.GeminiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;

    @PostMapping("/task/description")
    public ResponseEntity<ApiResponse<AiResponse>> generateTaskDescription(
        @Valid @RequestBody AiRequest request
    ){
            AiResponse response = geminiService.generateTaskDescription(request);
            return ResponseEntity.ok(ApiResponse.success("Task description generated successfully",response));
    }


    // ─── Generate Sprint Summary ──────────────────────────────────────────────

    @PostMapping("/sprint/summary")
    public ResponseEntity<ApiResponse<AiResponse>> generateSprintSummary(
            @Valid @RequestBody AiRequest request) {

        AiResponse response = geminiService.generateSprintSummary(request);
        return ResponseEntity.ok(ApiResponse.success("Sprint summary generated successfully", response));
    }

    // ─── Suggest Task Priority ────────────────────────────────────────────────

    @PostMapping("/task/priority")
    public ResponseEntity<ApiResponse<AiResponse>> suggestTaskPriority(
            @Valid @RequestBody AiRequest request) {

        AiResponse response = geminiService.suggestTaskPriority(request);
        return ResponseEntity.ok(ApiResponse.success("Task priority suggested successfully", response));
    }

}

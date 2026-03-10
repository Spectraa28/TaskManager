package com.Project.TaskManager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.Project.TaskManager.exceptions.BadRequestException;
import com.Project.TaskManager.payload.request.AiRequest;
import com.Project.TaskManager.payload.request.GeminiRequest;
import com.Project.TaskManager.payload.response.AiResponse;
import com.Project.TaskManager.payload.response.GeminiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeminiServiceImpl implements GeminiService{

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

   public GeminiServiceImpl() {
    this.webClient = WebClient.create();
     }

    @Override
    public AiResponse generateTaskDescription(AiRequest request){
        String prompt = buildDescriptionPrompt(request);
        String result = callGemini(prompt);
        return AiResponse.description(result, request.getTitle());
    }

    @Override
    public AiResponse generateSprintSummary(AiRequest request) {
        String prompt = buildSprintSummaryPrompt(request);
        String result = callGemini(prompt);
        return AiResponse.summary(result, request.getSprintName());
    }

    @Override
    public AiResponse suggestTaskPriority(AiRequest request) {
        String prompt = buildPriorityPrompt(request);
        String result = callGemini(prompt);
        return AiResponse.priority(result, request.getTitle());
    }

    private String callGemini(String prompt) {
        try {
            log.info("Calling Gemini API with prompt length: {}", prompt.length());

            GeminiRequest geminiRequest = GeminiRequest.of(prompt);

            GeminiResponse response = webClient
                    .post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(geminiRequest)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block(); // block() makes it synchronous — simpler for now

            if (response == null) {
                throw new BadRequestException("No response received from Gemini API");
            }

            String result = response.extractText();
            log.info("Gemini API call successful, response length: {}", result.length());
            return result;

        } catch (WebClientResponseException e) {
            log.error("Gemini API error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BadRequestException("Gemini API error: " + e.getMessage());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling Gemini API", e);
            throw new BadRequestException("Failed to get AI response: " + e.getMessage());
        }
    }

    // ─── Prompt Builders ──────────────────────────────────────────────────────

    // Good prompts = good AI output
    // We give Gemini clear instructions + context + output format

    private String buildDescriptionPrompt(AiRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a senior software engineer writing task descriptions for a project management tool.\n\n");
        prompt.append("Generate a clear, concise, and professional task description for the following task:\n\n");
        prompt.append("Task Title: ").append(request.getTitle()).append("\n");

        if (request.getContext() != null && !request.getContext().isBlank()) {
            prompt.append("Additional Context: ").append(request.getContext()).append("\n");
        }

        prompt.append("\nThe description should:\n");
        prompt.append("- Be 2-4 sentences long\n");
        prompt.append("- Explain what needs to be done and why\n");
        prompt.append("- Be written from a developer's perspective\n");
        prompt.append("- Be actionable and specific\n");
        prompt.append("- Not include the task title in the description\n\n");
        prompt.append("Return only the description text. No headings, no bullet points, no extra formatting.");

        return prompt.toString();
    }

    private String buildSprintSummaryPrompt(AiRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a scrum master writing a sprint summary for a software development team.\n\n");
        prompt.append("Generate a concise sprint summary for the following sprint:\n\n");
        prompt.append("Sprint Name: ").append(request.getSprintName()).append("\n");

        if (request.getTasksSummary() != null && !request.getTasksSummary().isBlank()) {
            prompt.append("Tasks completed in this sprint:\n").append(request.getTasksSummary()).append("\n");
        }

        prompt.append("\nThe summary should:\n");
        prompt.append("- Be 3-5 sentences long\n");
        prompt.append("- Highlight what was accomplished\n");
        prompt.append("- Be written in past tense\n");
        prompt.append("- Sound professional and be suitable for stakeholder reporting\n\n");
        prompt.append("Return only the summary text. No headings, no bullet points, no extra formatting.");

        return prompt.toString();
    }

    private String buildPriorityPrompt(AiRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a senior software engineer helping to prioritize tasks in a project management tool.\n\n");
        prompt.append("Suggest the appropriate priority level for the following task:\n\n");
        prompt.append("Task Title: ").append(request.getTitle()).append("\n");

        if (request.getContext() != null && !request.getContext().isBlank()) {
            prompt.append("Additional Context: ").append(request.getContext()).append("\n");
        }

        prompt.append("\nPriority levels available:\n");
        prompt.append("- LOW: Nice to have, no urgency, can be deferred\n");
        prompt.append("- MEDIUM: Important but not urgent, should be done this sprint\n");
        prompt.append("- HIGH: Urgent and important, needs immediate attention\n");
        prompt.append("- CRITICAL: Blocking other work or affecting production, must be done now\n\n");
        prompt.append("Respond in this exact format:\n");
        prompt.append("Priority: <LEVEL>\n");
        prompt.append("Reason: <one sentence explanation>\n\n");
        prompt.append("Return only the priority and reason. Nothing else.");

        return prompt.toString();
    }
    
}

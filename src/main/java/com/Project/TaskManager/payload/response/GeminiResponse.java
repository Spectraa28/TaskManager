package com.Project.TaskManager.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    private List<Candidate> candidates;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate{
        private Content content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content{
        private List<Part> parts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part{
        private String text;
    }

    public String extractText(){
        if(candidates == null || candidates.isEmpty()){
            return "AI responnse Unavailable";
        }

        Candidate candidate = candidates.get(0);

        if(candidate.getContent() == null || 
           candidate.getContent().getParts() == null ||
           candidate.getContent().getParts().isEmpty()){
            return "AI response unavailable";
           }
           return candidate.getContent().getParts().get(0).getText();
    }

    
}

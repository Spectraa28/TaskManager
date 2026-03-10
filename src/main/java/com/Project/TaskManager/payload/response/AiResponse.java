package com.Project.TaskManager.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResponse {
    private String type;

    private  String result;

    private String generatedFor;

    public static AiResponse description(String result , String taskTitle){
        return AiResponse.builder()
                        .type("DESCRIPTION")
                        .result(result)
                        .generatedFor(taskTitle)
                        .build();
    }

    public static AiResponse summary(String result, String sprintName){
        return AiResponse.builder()
                        .type("SUMMARY")
                        .result(result)
                        .generatedFor(sprintName)
                        .build();
    }

    public static AiResponse priority(String result,  String taskTitle){
        return AiResponse.builder()
                        .type("PRIORITY")
                        .result(result)
                        .generatedFor(taskTitle)
                        .build();
    }
}

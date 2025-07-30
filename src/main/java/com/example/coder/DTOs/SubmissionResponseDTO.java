package com.example.coder.DTOs;

import lombok.Data;

@Data
public class SubmissionResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long exerciseId;
    private String exerciseTitle;
    private Long languageId;
    private String languageName;
    private String sourceCode;
    private String status;
    private String stdout;
    private String stderr;
    private String compileOutput;
    private Double time;
    private String createdAt;
}

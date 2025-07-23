package com.example.coder.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SubmissionCreateDTO {
    @NotNull(message = "Exercise ID không được null")
    private Long exerciseId;

    @NotNull(message = "Language ID không được null")
    private Long languageId;

    @NotBlank(message = "Source code không được trống")
    private String sourceCode;
}

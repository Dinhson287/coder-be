package com.example.coder.DTOs;

import lombok.Data;

@Data
public class SubmissionUpdateDTO {
    private String status;
    private String stdout;
    private String stderr;
    private String compileOutput;
    private Double time;
}

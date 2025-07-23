package com.example.coder.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercises")
public class Exercises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", columnDefinition = "ENUM('EASY', 'MEDIUM', 'HARD')")
    private Difficulty difficulty;

    @Column(name = "sample_input", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "sample_output", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "created_at",
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",
            updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD;

        @JsonCreator
        public static Difficulty fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return EASY;
            }

            try {
                return Difficulty.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid difficulty: " + value + ". Valid values are: EASY, MEDIUM, HARD");
            }
        }

        @JsonValue
        public String toValue() {
            return this.name().toLowerCase();
        }
    }
}

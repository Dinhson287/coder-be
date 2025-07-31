package com.example.coder.controller;

import com.example.coder.DTOs.SubmissionCreateDTO;
import com.example.coder.DTOs.SubmissionResponseDTO;
import com.example.coder.DTOs.SubmissionUpdateDTO;
import com.example.coder.model.Users;
import com.example.coder.security.CustomUserDetailsService;
import com.example.coder.services.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<?> createSubmission(@Valid @RequestBody SubmissionCreateDTO dto) {
        try {
            SubmissionResponseDTO response = submissionService.createSubmission(dto.getUserId(), dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDTO> getSubmission(@PathVariable Long id) {
        SubmissionResponseDTO response = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-submissions")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissions(@RequestParam Long userId) {
        List<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByUser(userId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/my-submissions/paged")
    public ResponseEntity<Page<SubmissionResponseDTO>> getMySubmissionsPaged(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByUserPaged(userId, pageable);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissionsByExercise(@PathVariable Long exerciseId) {
        List<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByExercise(exerciseId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/exercise/{exerciseId}/my-submissions")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissionsForExercise(
            @PathVariable Long exerciseId,
            @RequestParam Long userId) {
        List<SubmissionResponseDTO> submissions = submissionService.getUserSubmissionsForExercise(userId, exerciseId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/exercise/{exerciseId}/latest-success")
    public ResponseEntity<SubmissionResponseDTO> getLatestSuccessfulSubmission(
            @PathVariable Long exerciseId,
            @RequestParam Long userId) {
        Optional<SubmissionResponseDTO> submission = submissionService.getLatestSuccessfulSubmission(userId, exerciseId);
        return submission.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubmissionResponseDTO>> getPendingSubmissions(Authentication auth) {
        List<SubmissionResponseDTO> submissions = submissionService.getPendingSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<SubmissionResponseDTO> updateSubmissionResult(
            @PathVariable Long id,
            @RequestBody SubmissionUpdateDTO dto) {
        try {
            SubmissionResponseDTO response = submissionService.updateSubmissionResult(id, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/admin-result")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubmissionResponseDTO> updateSubmissionResultByAdmin(
            @PathVariable Long id,
            @RequestBody SubmissionUpdateDTO dto,
            Authentication auth) {
        SubmissionResponseDTO response = submissionService.updateSubmissionResult(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/my-stats")
    public ResponseEntity<List<Object[]>> getMySubmissionStats(@RequestParam Long userId) {
        List<Object[]> stats = submissionService.getSubmissionStatsByUser(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-language")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getSubmissionStatsByLanguage(Authentication auth) {
        List<Object[]> stats = submissionService.getSubmissionStatsByLanguage();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id, @RequestParam Long userId) {
        submissionService.deleteSubmission(id, userId, false);
        return ResponseEntity.noContent().build();
    }
}
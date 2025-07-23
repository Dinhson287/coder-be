package com.example.coder.controller;

import com.example.coder.DTOs.SubmissionCreateDTO;
import com.example.coder.DTOs.SubmissionResponseDTO;
import com.example.coder.DTOs.SubmissionUpdateDTO;
import com.example.coder.services.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponseDTO> createSubmission(
            @Valid @RequestBody SubmissionCreateDTO dto,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        SubmissionResponseDTO response = submissionService.createSubmission(userId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDTO> getSubmission(@PathVariable Long id) {
        SubmissionResponseDTO response = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(response);
    }

    // Lấy submissions của user hiện tại
    @GetMapping("/my-submissions")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissions(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        List<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByUser(userId);
        return ResponseEntity.ok(submissions);
    }

    // Lấy submissions của user với pagination
    @GetMapping("/my-submissions/paged")
    public ResponseEntity<Page<SubmissionResponseDTO>> getMySubmissionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByUserPaged(userId, pageable);
        return ResponseEntity.ok(submissions);
    }

    // Lấy submissions theo exercise
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissionsByExercise(@PathVariable Long exerciseId) {
        List<SubmissionResponseDTO> submissions = submissionService.getSubmissionsByExercise(exerciseId);
        return ResponseEntity.ok(submissions);
    }

    // Lấy submissions của user cho exercise cụ thể
    @GetMapping("/exercise/{exerciseId}/my-submissions")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissionsForExercise(
            @PathVariable Long exerciseId,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        List<SubmissionResponseDTO> submissions = submissionService.getUserSubmissionsForExercise(userId, exerciseId);
        return ResponseEntity.ok(submissions);
    }

    // Lấy submission thành công gần nhất
    @GetMapping("/exercise/{exerciseId}/latest-success")
    public ResponseEntity<SubmissionResponseDTO> getLatestSuccessfulSubmission(
            @PathVariable Long exerciseId,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        Optional<SubmissionResponseDTO> submission = submissionService.getLatestSuccessfulSubmission(userId, exerciseId);
        return submission.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Admin: Lấy submissions pending
    @GetMapping("/pending")
    public ResponseEntity<List<SubmissionResponseDTO>> getPendingSubmissions() {
        List<SubmissionResponseDTO> submissions = submissionService.getPendingSubmissions();
        return ResponseEntity.ok(submissions);
    }

    // Admin: Cập nhật kết quả submission
    @PutMapping("/{id}/result")
    public ResponseEntity<SubmissionResponseDTO> updateSubmissionResult(
            @PathVariable Long id,
            @RequestBody SubmissionUpdateDTO dto) {
        SubmissionResponseDTO response = submissionService.updateSubmissionResult(id, dto);
        return ResponseEntity.ok(response);
    }

    // Thống kê submissions của user
    @GetMapping("/stats/my-stats")
    public ResponseEntity<List<Object[]>> getMySubmissionStats(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        List<Object[]> stats = submissionService.getSubmissionStatsByUser(userId);
        return ResponseEntity.ok(stats);
    }

    // Admin: Thống kê submissions theo language
    @GetMapping("/stats/by-language")
    public ResponseEntity<List<Object[]>> getSubmissionStatsByLanguage() {
        List<Object[]> stats = submissionService.getSubmissionStatsByLanguage();
        return ResponseEntity.ok(stats);
    }

    // Xóa submission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id, Authentication auth) {
        Long userId = getCurrentUserId(auth);
        boolean isAdmin = isCurrentUserAdmin(auth);
        submissionService.deleteSubmission(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId(Authentication auth) {
        return 1;
    }

    private boolean isCurrentUserAdmin(Authentication auth) {
        return false;
    }
}

package com.example.coder.services;

import com.example.coder.DTOs.SubmissionCreateDTO;
import com.example.coder.DTOs.SubmissionResponseDTO;
import com.example.coder.DTOs.SubmissionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SubmissionService {
    SubmissionResponseDTO createSubmission(Long userId, SubmissionCreateDTO dto);

    SubmissionResponseDTO getSubmissionById(Long id);

    List<SubmissionResponseDTO> getSubmissionsByUser(Long userId);

    Page<SubmissionResponseDTO> getSubmissionsByUserPaged(Long userId, Pageable pageable);

    List<SubmissionResponseDTO> getSubmissionsByExercise(Long exerciseId);

    List<SubmissionResponseDTO> getPendingSubmissions();

    SubmissionResponseDTO updateSubmissionResult(Long id, SubmissionUpdateDTO dto);

    List<SubmissionResponseDTO> getUserSubmissionsForExercise(Long userId, Long exerciseId);

    Optional<SubmissionResponseDTO> getLatestSuccessfulSubmission(Long userId, Long exerciseId);

    List<Object[]> getSubmissionStatsByUser(Long userId);

    List<Object[]> getSubmissionStatsByLanguage();

    void deleteSubmission(Long id, Long currentUserId, boolean isAdmin);

    Page<SubmissionResponseDTO> getSubmissionsByUserPagedWithFilters(
            Long userId,
            Pageable pageable,
            Long languageId,
            String status,
            String exerciseKeyword
    );
}

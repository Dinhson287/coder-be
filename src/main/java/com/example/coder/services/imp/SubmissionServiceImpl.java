package com.example.coder.services.imp;

import com.example.coder.DTOs.SubmissionCreateDTO;
import com.example.coder.DTOs.SubmissionResponseDTO;
import com.example.coder.DTOs.SubmissionUpdateDTO;
import com.example.coder.model.Exercises;
import com.example.coder.model.Languages;
import com.example.coder.model.Submission;
import com.example.coder.model.Users;
import com.example.coder.repo.ExercisesRepo;
import com.example.coder.repo.LanguagesRepo;
import com.example.coder.repo.SubmissionRepo;
import com.example.coder.repo.UsersRepo;
import com.example.coder.services.SubmissionService;
import com.example.coder.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final UsersRepo usersRepo;
    private final ExercisesRepo exercisesRepo;
    private final LanguagesRepo languagesRepo;

    @Override
    public SubmissionResponseDTO createSubmission(Long userId, SubmissionCreateDTO dto) {
        try {
            Users user = usersRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            Exercises exercise = exercisesRepo.findById(dto.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + dto.getExerciseId()));

            Languages language = languagesRepo.findById(dto.getLanguageId())
                    .orElseThrow(() -> new RuntimeException("Language not found with ID: " + dto.getLanguageId()));

            System.out.println("Creating submission for:");
            System.out.println("- User: " + user.getUsername());
            System.out.println("- Exercise: " + exercise.getTitle());
            System.out.println("- Language: " + language.getName() + " (Judge0 Code: " + language.getCode() + ")");

            Submission submission = new Submission();
            submission.setUser(user);
            submission.setExercise(exercise);
            submission.setLanguage(language);
            submission.setSourceCode(dto.getSourceCode());
            submission.setStatus(Submission.Status.PENDING);

            Submission saved = submissionRepo.save(submission);

            executeCodeAsync(saved.getId(), language.getCode());

            return convertToResponseDTO(saved);
        } catch (Exception e) {
            System.err.println("Error creating submission: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create submission: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponseDTO getSubmissionById(Long id) {
        Submission submission = submissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission không tồn tại"));
        return convertToResponseDTO(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getSubmissionsByUser(Long userId) {
        return submissionRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponseDTO> getSubmissionsByUserPaged(Long userId, Pageable pageable) {
        return submissionRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getSubmissionsByExercise(Long exerciseId) {
        return submissionRepo.findByExerciseIdOrderByCreatedAtDesc(exerciseId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getPendingSubmissions() {
        return submissionRepo.findByStatusOrderByCreatedAtAsc(Submission.Status.PENDING)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponseDTO updateSubmissionResult(Long id, SubmissionUpdateDTO dto) {
        Submission submission = submissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission không tồn tại"));

        if (dto.getStatus() != null) {
            submission.setStatus(Submission.Status.valueOf(dto.getStatus()));
        }
        submission.setStdout(dto.getStdout());
        submission.setStderr(dto.getStderr());
        submission.setCompileOutput(dto.getCompileOutput());
        submission.setTime(dto.getTime());

        Submission updated = submissionRepo.save(submission);
        return convertToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getUserSubmissionsForExercise(Long userId, Long exerciseId) {
        return submissionRepo.findByUserIdAndExerciseIdOrderByCreatedAtDesc(userId, exerciseId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubmissionResponseDTO> getLatestSuccessfulSubmission(Long userId, Long exerciseId) {
        List<Submission> submissions = submissionRepo.findLatestSuccessfulSubmission(userId, exerciseId);
        return submissions.isEmpty() ? Optional.empty() :
                Optional.of(convertToResponseDTO(submissions.get(0)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSubmissionStatsByUser(Long userId) {
        return submissionRepo.countSubmissionsByStatusForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSubmissionStatsByLanguage() {
        return submissionRepo.countSubmissionsByLanguage();
    }

    @Override
    public void deleteSubmission(Long id, Long currentUserId, boolean isAdmin) {
        Submission submission = submissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission không tồn tại"));

        if (!isAdmin && !submission.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Không có quyền xóa submission này");
        }

        submissionRepo.delete(submission);
    }

    private void executeCodeAsync(Long submissionId, Integer judge0LanguageCode) {
        System.out.println("Executing code for submission " + submissionId + " with Judge0 language code: " + judge0LanguageCode);
    }

    private SubmissionResponseDTO convertToResponseDTO(Submission submission) {
        SubmissionResponseDTO dto = new SubmissionResponseDTO();
        dto.setId(submission.getId());
        dto.setUserId(submission.getUser().getId());
        dto.setUsername(submission.getUser().getUsername());
        dto.setExerciseId(submission.getExercise().getId());
        dto.setExerciseTitle(submission.getExercise().getTitle());
        dto.setLanguageId(submission.getLanguage().getId());
        dto.setLanguageName(submission.getLanguage().getName());
        dto.setSourceCode(submission.getSourceCode());
        dto.setStatus(submission.getStatus().name());
        dto.setStdout(submission.getStdout());
        dto.setStderr(submission.getStderr());
        dto.setCompileOutput(submission.getCompileOutput());
        dto.setTime(submission.getTime());
        dto.setCreatedAt(submission.getCreatedAt().toString());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponseDTO> getSubmissionsByUserPagedWithFilters(
            Long userId,
            Pageable pageable,
            Long languageId,
            String status,
            String exerciseKeyword) {

        Submission.Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Submission.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                statusEnum = null;
            }
        }

        return submissionRepo.findByUserIdWithFilters(
                userId,
                languageId,
                statusEnum,
                exerciseKeyword,
                pageable
        ).map(this::convertToResponseDTO);
    }
}

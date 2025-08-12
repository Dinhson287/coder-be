package com.example.coder.repo;

import com.example.coder.model.Submission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Submission> findByExerciseIdOrderByCreatedAtDesc(Long exerciseId);

    List<Submission> findByStatusOrderByCreatedAtDesc(Submission.Status status);

    List<Submission> findByStatusOrderByCreatedAtAsc(Submission.Status status);

    List<Submission> findByUserIdAndExerciseIdOrderByCreatedAtDesc(Long userId, Long exerciseId);

    Page<Submission> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT s.status, COUNT(s) FROM Submission s WHERE s.user.id = :userId GROUP BY s.status")
    List<Object[]> countSubmissionsByStatusForUser(@Param("userId") Long userId);

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.exercise.id = :exerciseId AND s.status = 'SUCCESS' ORDER BY s.createdAt DESC")
    List<Submission> findLatestSuccessfulSubmission(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    @Query("SELECT l.name, COUNT(s) FROM Submission s JOIN s.language l GROUP BY l.name")
    List<Object[]> countSubmissionsByLanguage();

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId " +
            "AND (:languageId IS NULL OR s.language.id = :languageId) " +
            "AND (:status IS NULL OR s.status = :status) " +
            "AND (:exerciseKeyword IS NULL OR LOWER(s.exercise.title) LIKE LOWER(CONCAT('%', :exerciseKeyword, '%'))) " +
            "ORDER BY s.createdAt DESC")
    Page<Submission> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("languageId") Long languageId,
            @Param("status") Submission.Status status,
            @Param("exerciseKeyword") String exerciseKeyword,
            Pageable pageable
    );
}

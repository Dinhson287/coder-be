package com.example.coder.repo;


import com.example.coder.model.Exercises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExercisesRepo extends JpaRepository<Exercises, Long> {
    @Query("select e from Exercises e where lower(e.title) = lower(:title)")
    Optional<Exercises> findByTitle(@Param("title") String title);

    @Query("select count(e) from Exercises  e where  lower(e.title) = lower(:title)")
    boolean existsByTitle(@Param("title") String title);

    List<Exercises> findByDifficulty(Exercises.Difficulty difficulty);

    @Query("select e from Exercises e where lower(e.title) like lower(concat('%',:keyword,'%'))")
    List<Exercises> findByTitleLike(@Param("keyword") String keyword);

    List<Exercises> findAllByOrderByCreatedAtDesc();

    List<Exercises> findAllByOrderByCreatedAtAsc();
}

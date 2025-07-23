package com.example.coder.repo;

import com.example.coder.model.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguagesRepo extends JpaRepository<Languages, Long> {
    @Query("SELECT l FROM Languages l WHERE LOWER(l.name) = LOWER(:name)")
    Optional<Languages> findByNameIgnoreCase(@Param("name") String name);

    Optional<Languages> findByCode(Integer code);

    @Query("SELECT l FROM Languages l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Languages> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    List<Languages> findAllByOrderByNameAsc();

    List<Languages> findAllByOrderByCodeAsc();
}

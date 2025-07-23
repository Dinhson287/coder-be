package com.example.coder.services.imp;

import com.example.coder.model.Exercises;
import com.example.coder.repo.ExercisesRepo;
import com.example.coder.services.ExcercisesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ExercisesServiceImp implements ExcercisesService {

    private final ExercisesRepo exercisesRepo;

    @Override
    @Transactional
    public ResponseEntity<Exercises> addExercise(Exercises exercises) {
        validateExercise(exercises);
        Exercises savedExercises = exercisesRepo.save(exercises);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercises);
    }

    private void validateExercise(Exercises exercise) {
        if (exercise == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise object cannot be null");
        }

        validateTitle(exercise.getTitle());
        validateDescription(exercise.getDescription());

        checkTitleDuplicate(exercise.getTitle(), null);
    }

    private void checkTitleDuplicate(String title, Long excludeId) {
        Optional<Exercises> existingExercise = exercisesRepo.findByTitle(title);
        if (existingExercise.isPresent()) {
            if (excludeId != null && existingExercise.get().getId().equals(excludeId)) {
                return;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Exercise title already exists");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise title cannot be empty");
        }
        if (title.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise title must be less than 255 characters");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise description cannot be empty");
        }
        if (description.length() > 65535) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise description is too long");
        }
    }

    @Override
    public ResponseEntity<List<Exercises>> getAllExercises() {
        List<Exercises> exercises = exercisesRepo.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(exercises);
    }

    @Override
    public ResponseEntity<Exercises> getExerciseById(Long id) {
        if(id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid exercise ID");
        }
        Optional<Exercises> exercises = exercisesRepo.findById(id);
        if(exercises.isPresent()) {
            return ResponseEntity.ok(exercises.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found");
        }
    }

    @Override
    public ResponseEntity<List<Exercises>> getExercisesByDifficulty(String title) {
        try {
            Exercises.Difficulty difficulty = Exercises.Difficulty.fromString(title);
            List<Exercises> exercises = exercisesRepo.findByDifficulty(difficulty);
            return ResponseEntity.ok(exercises);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Exercises>> getExercisesByTitle(String keyword){
        if(keyword == null || keyword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keyword cannot be empty");
        }
        List<Exercises> exercises = exercisesRepo.findByTitleLike(keyword.trim());
        return ResponseEntity.ok(exercises);
    }

    @Override
    public ResponseEntity<Exercises> updateExercise(Long id, Exercises exercise){
        if(id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid exercise ID");
        }
        Optional<Exercises> existingExerciseOpt  = exercisesRepo.findById(id);
        if(!existingExerciseOpt .isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found");
        }

        Exercises existingExercise  = existingExerciseOpt .get();
        validateExerciseForUpdate(id, exercise);

        if (exercise.getTitle() != null && !exercise.getTitle().trim().isEmpty()) {
            existingExercise.setTitle(exercise.getTitle().trim());
        }
        if (exercise.getDescription() != null && !exercise.getDescription().trim().isEmpty()) {
            existingExercise.setDescription(exercise.getDescription().trim());
        }
        if (exercise.getDifficulty() != null) {
            existingExercise.setDifficulty(exercise.getDifficulty());
        }
        if (exercise.getSampleInput() != null) {
            existingExercise.setSampleInput(exercise.getSampleInput().trim());
        }
        if (exercise.getSampleOutput() != null) {
            existingExercise.setSampleOutput(exercise.getSampleOutput().trim());
        }
        Exercises updatedExercise = exercisesRepo.save(existingExercise);
        return ResponseEntity.ok(updatedExercise);
    }

    @Override
    public ResponseEntity<Exercises> deleteExercise(Long id) {
        if(id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid exercise ID");
        }
        if(!exercisesRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found");
        }

        exercisesRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void validateExerciseForUpdate(Long id, Exercises exercise) {
        if (exercise == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise object cannot be null");
        }

        if (exercise.getTitle() != null && !exercise.getTitle().trim().isEmpty()) {
            validateTitle(exercise.getTitle());
            Optional<Exercises> existingExercise = exercisesRepo.findByTitle(exercise.getTitle());
            if (existingExercise.isPresent() && !existingExercise.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Exercise title already exists");
            }
        }

        if (exercise.getDescription() != null && !exercise.getDescription().trim().isEmpty()) {
            validateDescription(exercise.getDescription());
        }
    }
}

package com.example.coder.controller;

import com.example.coder.model.Exercises;
import com.example.coder.services.ExcercisesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExercisesController {
    private final ExcercisesService excercisesService;

    @PostMapping
    public ResponseEntity<Exercises> addExercise(@RequestBody Exercises exercises) {
        try {
            return excercisesService.addExercise(exercises);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Exercises>> getAllExercises() {
        try {
            return excercisesService.getAllExercises();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercises> getExerciseById(@PathVariable Long id) {
        try {
            return excercisesService.getExerciseById(id);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Exercises>> getExercisesByDifficulty(@PathVariable String difficulty) {
        try {
            return excercisesService.getExercisesByDifficulty(difficulty);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Exercises>> getExercisesByTitle(@RequestParam String keyword) {
        try {
            return excercisesService.getExercisesByTitle(keyword);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exercises> updateExercise(@PathVariable Long id, @RequestBody Exercises exercise) {
        try {
            return excercisesService.updateExercise(id,exercise);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Exercises> deleteExercise(@PathVariable Long id) {
        try {
            return excercisesService.deleteExercise(id);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

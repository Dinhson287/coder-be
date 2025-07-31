package com.example.coder.controller;

import com.example.coder.model.Exercises;
import com.example.coder.services.ExcercisesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<Exercises>> getExercisesByTopic(@PathVariable String topic) {
        try {
            return excercisesService.getExercisesByTopic(topic);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/topic/{topic}/difficulty/{difficulty}")
    public ResponseEntity<List<Exercises>> getExercisesByTopicAndDifficulty(
            @PathVariable String topic,
            @PathVariable String difficulty) {
        try {
            return excercisesService.getExercisesByTopicAndDifficulty(topic, difficulty);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/topics")
    public ResponseEntity<List<String>> getAllTopics() {
        try {
            return excercisesService.getAllTopics();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<Exercises>> searchExercises(@RequestParam String keyword) {
        try {
            return excercisesService.searchExercises(keyword);
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

    @GetMapping("/paged")
    public ResponseEntity<Page<Exercises>> getAllExercisesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return excercisesService.getAllExercisesPaged(page, size);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
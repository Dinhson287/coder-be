package com.example.coder.services;

import com.example.coder.model.Exercises;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExcercisesService {
    ResponseEntity<Exercises> addExercise(Exercises exercise);
    ResponseEntity<List<Exercises>> getAllExercises();
    ResponseEntity<Exercises> getExerciseById(Long id);
    ResponseEntity<List<Exercises>> getExercisesByDifficulty(String difficulty);
    ResponseEntity<List<Exercises>> getExercisesByTitle(String keyword);
    ResponseEntity<List<Exercises>> getExercisesByTopic(String topic);
    ResponseEntity<List<Exercises>> getExercisesByTopicAndDifficulty(String topic, String difficulty);
    ResponseEntity<List<String>> getAllTopics();
    ResponseEntity<List<Exercises>> searchExercises(String keyword);
    ResponseEntity<Exercises> updateExercise(Long id, Exercises exercises);
    ResponseEntity<Exercises> deleteExercise(Long id);
}

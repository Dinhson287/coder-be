package com.example.coder.controller;

import com.example.coder.model.Languages;
import com.example.coder.services.LanguagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
public class LanguagesController {

    private final LanguagesService languagesService;

    @PostMapping
    public ResponseEntity<Languages> addLanguage(@RequestBody Languages language) {
        try {
            return languagesService.addLanguage(language);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Languages>> getAllLanguages() {
        try {
            return languagesService.getAllLanguages();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Languages> getLanguageById(@PathVariable Long id) {
        try {
            return languagesService.getLanguageById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Languages> updateLanguage(@PathVariable Long id, @RequestBody Languages language) {
        try {
            return languagesService.updateLanguage(id, language);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        try {
            return languagesService.deleteLanguage(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

package com.example.coder.services;

import com.example.coder.model.Languages;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LanguagesService {
    ResponseEntity<Languages> addLanguage(Languages language);
    ResponseEntity<List<Languages>> getAllLanguages();
    ResponseEntity<Languages> getLanguageById(Long id);
    ResponseEntity<Languages> updateLanguage(Long id, Languages language);
    ResponseEntity<Void> deleteLanguage(Long id);
}

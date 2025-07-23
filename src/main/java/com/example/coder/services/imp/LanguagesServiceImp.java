package com.example.coder.services.imp;

import com.example.coder.model.Languages;
import com.example.coder.repo.LanguagesRepo;
import com.example.coder.services.LanguagesService;
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
public class LanguagesServiceImp implements LanguagesService {
    private final LanguagesRepo languagesRepo;

    @Override
    @Transactional
    public ResponseEntity<Languages> addLanguage(Languages language) {
        validateLanguage(language);
        Languages savedLanguage = languagesRepo.save(language);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLanguage);
    }

    private void validateLanguage(Languages language) {
        if (language == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language object cannot be null");
        }

        validateName(language.getName());
        validateCode(language.getCode());

        checkNameDuplicate(language.getName(), null);
        checkCodeDuplicate(language.getCode(), null);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language name cannot be empty");
        }
        if (name.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language name must be less than 50 characters");
        }
    }

    private void validateCode(Integer code) {
        if (code == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language code cannot be null");
        }
        if (code <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language code must be greater than 0");
        }
        if (code > 999999) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language code must be less than 1000000");
        }
    }

    private void checkNameDuplicate(String name, Long excludeId) {
        Optional<Languages> existingLanguage = languagesRepo.findByNameIgnoreCase(name);
        if (existingLanguage.isPresent()) {
            if (excludeId != null && existingLanguage.get().getId().equals(excludeId)) {
                return;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Language name already exists");
        }
    }

    private void checkCodeDuplicate(Integer code, Long excludeId) {
        Optional<Languages> existingLanguage = languagesRepo.findByCode(code);
        if (existingLanguage.isPresent()) {
            if (excludeId != null && existingLanguage.get().getId().equals(excludeId)) {
                return;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Language code already exists");
        }
    }

    @Override
    public ResponseEntity<List<Languages>> getAllLanguages() {
        List<Languages> languages = languagesRepo.findAllByOrderByNameAsc();
        return ResponseEntity.ok(languages);
    }

    @Override
    public ResponseEntity<Languages> getLanguageById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid language ID");
        }

        Optional<Languages> language = languagesRepo.findById(id);
        if (language.isPresent()) {
            return ResponseEntity.ok(language.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found with ID: " + id);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Languages> updateLanguage(Long id, Languages language) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid language ID");
        }

        Optional<Languages> existingLanguageOpt = languagesRepo.findById(id);
        if (!existingLanguageOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found with ID: " + id);
        }

        Languages existingLanguage = existingLanguageOpt.get();

        validateLanguageForUpdate(id, language);

        if (language.getName() != null && !language.getName().trim().isEmpty()) {
            existingLanguage.setName(language.getName().trim());
        }
        if (language.getCode() != null) {
            existingLanguage.setCode(language.getCode());
        }

        Languages updatedLanguage = languagesRepo.save(existingLanguage);
        return ResponseEntity.ok(updatedLanguage);
    }

    private void validateLanguageForUpdate(Long id, Languages language) {
        if (language == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language object cannot be null");
        }

        if (language.getName() != null && !language.getName().trim().isEmpty()) {
            validateName(language.getName());
            checkNameDuplicate(language.getName(), id);
        }

        if (language.getCode() != null) {
            validateCode(language.getCode());
            checkCodeDuplicate(language.getCode(), id);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteLanguage(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid language ID");
        }

        if (!languagesRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found with ID: " + id);
        }

        languagesRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

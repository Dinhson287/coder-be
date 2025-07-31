package com.example.coder.services.imp;

import com.example.coder.model.Users;
import com.example.coder.repo.UsersRepo;
import com.example.coder.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersServiceImp implements UsersService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<Users> addUser(Users user) {
        validateUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Users savedUser = usersRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Override
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersRepo.findAll();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<Users> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }

        Optional<Users> user = usersRepo.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }
    }

    @Override
    public ResponseEntity<Users> updateUser(Long id, Users user) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }

        Optional<Users> existingUserOpt = usersRepo.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }

        Users existingUser = existingUserOpt.get();

        validateUserForUpdate(id, user);

        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            existingUser.setUsername(user.getUsername().trim());
        }
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            existingUser.setEmail(user.getEmail().trim());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        Users updatedUser = usersRepo.save(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }

        if (!usersRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }

        usersRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public Page<Users> getAllUsersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usersRepo.findAll(pageable);
    }

    private void validateUser(Users user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User object cannot be null");
        }

        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
        validateEmail(user.getEmail());

        if (usersRepo.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (usersRepo.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    private void validateUserForUpdate(Long id, Users user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User object cannot be null");
        }

        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            validateUsername(user.getUsername());
            Optional<Users> existingUser = usersRepo.findByUsername(user.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }
        }

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            validatePassword(user.getPassword());
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            validateEmail(user.getEmail());
            Optional<Users> existingUser = usersRepo.findByEmail(user.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must be between 3 and 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username can only contain letters, numbers and underscore");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty");
        }
        if (password.length() < 6 || password.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 6 and 100 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        if (email.length() > 150) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must be less than 150 characters");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }
}
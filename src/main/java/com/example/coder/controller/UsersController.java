package com.example.coder.controller;

import com.example.coder.DTOs.UserResponseDTO;
import com.example.coder.model.Users;
import com.example.coder.security.CustomUserDetailsService;
import com.example.coder.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> addUser(@RequestBody Users user) {
        try {
            Users savedUser = usersService.addUser(user).getBody();
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            return usersService.getAllUsers();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile(Authentication auth) {
        try {
            String username = auth.getName();
            Users user = userDetailsService.getUserByUsername(username);

            UserResponseDTO userResponse = new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getCreatedAt().toString()
            );

            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @usersController.isCurrentUser(authentication, #id))")
    public ResponseEntity<Users> getUserById(@PathVariable Long id, Authentication auth) {
        try {
            return usersService.getUserById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @usersController.isCurrentUser(authentication, #id))")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users user, Authentication auth) {
        try {
            if (!isCurrentUserAdmin(auth)) {
                user.setRole(null);
            }
            return usersService.updateUser(id, user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            return usersService.deleteUser(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public boolean isCurrentUser(Authentication auth, Long userId) {
        try {
            String username = auth.getName();
            Users user = userDetailsService.getUserByUsername(username);
            return user.getId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCurrentUserAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}

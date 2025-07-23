package com.example.coder.services;

import com.example.coder.model.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersService {
    ResponseEntity<Users> addUser(Users user);
    ResponseEntity<List<Users>> getAllUsers();
    ResponseEntity<Users> getUserById(Long id);
    ResponseEntity<Users> updateUser(Long id, Users user);
    ResponseEntity<Void> deleteUser(Long id);
}

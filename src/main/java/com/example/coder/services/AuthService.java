package com.example.coder.services;

import com.example.coder.DTOs.LoginRequestDTO;
import com.example.coder.DTOs.LoginResponseDTO;
import com.example.coder.DTOs.RegisterRequestDTO;
import com.example.coder.DTOs.UserResponseDTO;
import com.example.coder.model.Users;
import com.example.coder.repo.UsersRepo;
import com.example.coder.security.CustomUserDetailsService;
import com.example.coder.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Users user = usersRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        return new LoginResponseDTO(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                86400000L // 24 hours
        );
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        if (usersRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (usersRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Users.Role.USER);

        Users savedUser = usersRepo.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getCreatedAt().toString()
        );
    }

    public LoginResponseDTO refreshToken(String token) {
        String username = jwtUtil.extractUsername(token);
        Users user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (jwtUtil.canTokenBeRefreshed(token)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newToken = jwtUtil.generateToken(userDetails);

            return new LoginResponseDTO(
                    newToken,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    86400000L
            );
        }

        throw new RuntimeException("Token không thể refresh");
    }
}

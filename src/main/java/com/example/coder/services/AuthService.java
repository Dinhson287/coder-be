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
import org.springframework.security.authentication.BadCredentialsException;
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
        try {
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
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng");
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi xảy ra khi đăng nhập");
        }
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO request) {
        // Validate input
        validateRegisterInput(request);

        // Check for existing username
        if (usersRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Check for existing email
        if (usersRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        try {
            Users user = new Users();
            user.setUsername(request.getUsername().trim());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail().trim().toLowerCase());
            user.setRole(Users.Role.USER);

            Users savedUser = usersRepo.save(user);

            return new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getRole().name(),
                    savedUser.getCreatedAt().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi xảy ra khi tạo tài khoản");
        }
    }

    private void validateRegisterInput(RegisterRequestDTO request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên người dùng không được để trống");
        }

        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new RuntimeException("Tên người dùng phải từ 3-50 ký tự");
        }

        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new RuntimeException("Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Định dạng email không hợp lệ");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    public LoginResponseDTO refreshToken(String token) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Token không thể refresh");
        }
    }
}
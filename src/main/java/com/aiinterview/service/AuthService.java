package com.aiinterview.service;

import com.aiinterview.config.JwtUtil;
import com.aiinterview.dto.AuthRequest;
import com.aiinterview.dto.AuthResponse;
import com.aiinterview.dto.RegisterRequest;
import com.aiinterview.dto.UserProfileDto;
import com.aiinterview.model.Performance;
import com.aiinterview.model.User;
import com.aiinterview.repository.PerformanceRepository;
import com.aiinterview.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PerformanceRepository performanceRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        if (request.getTargetRole() != null && !request.getTargetRole().isBlank()) {
            user.setTargetRole(request.getTargetRole().trim());
        }
        user.setBio(request.getBio());

        User savedUser = userRepository.save(user);

        // Initialize user performance record
        Performance perf = new Performance();
        perf.setUser(savedUser);
        performanceRepository.save(perf);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails, savedUser.getRole(), savedUser.getId());

        return new AuthResponse(token, savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole(), savedUser.getTargetRole());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole(), user.getId());

        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getTargetRole());
    }

    public UserProfileDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return new UserProfileDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getTargetRole(), user.getBio());
    }

    @Transactional
    public UserProfileDto updateProfile(String email, UserProfileDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getTargetRole() != null && !dto.getTargetRole().isBlank()) {
            user.setTargetRole(dto.getTargetRole().trim());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio().trim());
        }

        User updated = userRepository.save(user);
        return new UserProfileDto(updated.getId(), updated.getFullName(), updated.getEmail(), updated.getRole(), updated.getTargetRole(), updated.getBio());
    }
}

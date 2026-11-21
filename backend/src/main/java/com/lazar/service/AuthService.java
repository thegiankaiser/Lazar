package com.lazar.service;

import com.lazar.dto.AuthDtos.*;
import com.lazar.exception.ApiException;
import com.lazar.model.AppUser;
import com.lazar.repository.UserRepository;
import com.lazar.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        AppUser user = users.save(new AppUser(email, encoder.encode(request.password())));
        return response(user);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = users.findByEmail(request.email().trim().toLowerCase())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPasswordHash()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return response(user);
    }

    private AuthResponse response(AppUser user) {
        return new AuthResponse(jwt.generate(user.getEmail()), "Bearer", jwt.expirationMs(), user.getEmail());
    }
}

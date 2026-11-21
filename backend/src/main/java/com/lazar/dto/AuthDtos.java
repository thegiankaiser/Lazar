package com.lazar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 10, max = 72) String password
    ) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String accessToken, String tokenType, long expiresInMs, String email) {}
}

package com.lazar.service;

import com.lazar.dto.AuthDtos.RegisterRequest;
import com.lazar.repository.UserRepository;
import com.lazar.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Test
    void registerStoresHashedPasswordAndReturnsToken() {
        UserRepository users = mock(UserRepository.class);
        JwtService jwt = mock(JwtService.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        when(users.existsByEmail("person@example.com")).thenReturn(false);
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwt.generate("person@example.com")).thenReturn("test.jwt.token");
        when(jwt.expirationMs()).thenReturn(3600000L);

        var service = new AuthService(users, encoder, jwt);
        var result = service.register(new RegisterRequest("Person@Example.com", "LongPassword123"));

        assertEquals("test.jwt.token", result.accessToken());
        verify(users).save(argThat(user -> encoder.matches("LongPassword123", user.getPasswordHash())));
    }
}

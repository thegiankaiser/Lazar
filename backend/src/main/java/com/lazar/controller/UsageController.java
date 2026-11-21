package com.lazar.controller;

import com.lazar.dto.ChatDtos.UsageResponse;
import com.lazar.model.AppUser;
import com.lazar.repository.UserRepository;
import com.lazar.service.UsageService;
import com.lazar.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usage")
public class UsageController {
    private final UserRepository users;
    private final UsageService usage;
    public UsageController(UserRepository users, UsageService usage) { this.users = users; this.usage = usage; }
    @GetMapping public UsageResponse status(Authentication auth) {
        AppUser user = users.findByEmail(auth.getName())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User account not found"));
        return usage.status(user);
    }
}

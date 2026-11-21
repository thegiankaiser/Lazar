package com.lazar.controller;

import com.lazar.dto.ChatDtos.*;
import com.lazar.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chats;
    public ChatController(ChatService chats) { this.chats = chats; }
    @PostMapping public ChatResponse chat(Authentication auth, @Valid @RequestBody ChatRequest request) {
        return chats.chat(auth.getName(), request);
    }
    @GetMapping("/history") public List<ChatResponse> history(Authentication auth) {
        return chats.history(auth.getName());
    }
}

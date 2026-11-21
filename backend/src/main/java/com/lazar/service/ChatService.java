package com.lazar.service;

import com.lazar.dto.ChatDtos.*;
import com.lazar.exception.ApiException;
import com.lazar.model.*;
import com.lazar.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatService {
    private final UserRepository users;
    private final ChatRecordRepository chats;
    private final AiService ai;
    private final UsageService usage;

    public ChatService(UserRepository users, ChatRecordRepository chats, AiService ai, UsageService usage) {
        this.users = users; this.chats = chats; this.ai = ai; this.usage = usage;
    }

    @Transactional
    public ChatResponse chat(String email, ChatRequest request) {
        AppUser user = users.findByEmail(email)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User account not found"));
        usage.consume(user);
        String output = ai.generate(request.message().trim(), request.workflow());
        ChatRecord saved = chats.save(new ChatRecord(user, request.workflow(), request.message().trim(), output));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> history(String email) {
        AppUser user = users.findByEmail(email)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User account not found"));
        return chats.findTop50ByUser_IdOrderByCreatedAtDesc(user.getId()).stream().map(this::toResponse).toList();
    }

    private ChatResponse toResponse(ChatRecord record) {
        return new ChatResponse(record.getId(), record.getWorkflow(), record.getInputText(), record.getOutputText(), record.getCreatedAt());
    }
}

package com.lazar.dto;

import com.lazar.model.Workflow;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class ChatDtos {
    private ChatDtos() {}
    public record ChatRequest(
        @NotBlank @Size(max = 5000) String message,
        @NotNull Workflow workflow
    ) {}
    public record ChatResponse(Long id, Workflow workflow, String input, String output, Instant createdAt) {}
    public record UsageResponse(int used, int dailyLimit, int remaining, String resetTimeUtc) {}
}

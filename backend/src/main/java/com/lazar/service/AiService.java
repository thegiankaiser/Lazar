package com.lazar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazar.config.LazarProperties;
import com.lazar.exception.ApiException;
import com.lazar.model.Workflow;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@Service
public class AiService {
    private final LazarProperties properties;
    private final WebClient client;
    private final ObjectMapper mapper;

    public AiService(LazarProperties properties, WebClient.Builder builder, ObjectMapper mapper) {
        this.properties = properties;
        this.client = builder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.mapper = mapper;
    }

    public String generate(String userMessage, Workflow workflow) {
        String key = properties.ai().apiKey();
        if (key == null || key.isBlank()) throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI service is not configured");
        String instruction = switch (workflow) {
            case ORGANIZE_TASKS -> "Turn the user's messy notes into prioritized tasks. Use headings: Priorities, Next Actions, Quick Wins. Be concise and do not invent deadlines.";
            case ACTION_PLAN -> "Create a practical step-by-step action plan. Include a first 10-minute action, dependencies, and a short checklist. Do not invent facts.";
            case DRAFT_EMAIL -> "Draft a clear, professional email based only on the user's information. Include a useful subject line and concise body. Use placeholders for missing names or facts.";
        };
        Map<String, Object> body = Map.of(
            "system_instruction", Map.of("parts", List.of(Map.of("text",
                "You are Lazar, a concise and supportive productivity assistant. " + instruction))),
            "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage)))),
            "generationConfig", Map.of("temperature", 0.4, "maxOutputTokens", 1200)
        );
        try {
            String raw = client.post()
                .uri(uri -> uri.path("/v1beta/models/{model}:generateContent")
                    .queryParam("key", key).build(properties.ai().model()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(45));
            JsonNode root = mapper.readTree(raw);
            JsonNode text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (text.isMissingNode() || text.asText().isBlank())
                throw new ApiException(HttpStatus.BAD_GATEWAY, "AI provider returned an empty response");
            String output = text.asText();
            return output.length() > 12000 ? output.substring(0, 12000) : output;
        } catch (WebClientResponseException ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI provider request failed");
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Could not complete AI request");
        }
    }
}

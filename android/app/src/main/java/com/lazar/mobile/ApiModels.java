package com.lazar.mobile;

public final class ApiModels {
    private ApiModels() {}
    public static class AuthRequest {
        public String email;
        public String password;
        public AuthRequest(String email, String password) { this.email = email; this.password = password; }
    }
    public static class AuthResponse {
        public String accessToken;
        public String tokenType;
        public long expiresInMs;
        public String email;
    }
    public static class ChatRequest {
        public String message;
        public String workflow;
        public ChatRequest(String message, String workflow) { this.message = message; this.workflow = workflow; }
    }
    public static class ChatResponse {
        public Long id;
        public String workflow;
        public String input;
        public String output;
        public String createdAt;
    }
}

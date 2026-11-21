package com.lazar.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_records", indexes = @Index(name = "idx_chat_user_created", columnList = "user_id, created_at"))
public class ChatRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Workflow workflow;
    @Column(nullable = false, length = 5000)
    private String inputText;
    @Column(nullable = false, length = 12000)
    private String outputText;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected ChatRecord() {}
    public ChatRecord(AppUser user, Workflow workflow, String inputText, String outputText) {
        this.user = user; this.workflow = workflow; this.inputText = inputText; this.outputText = outputText;
    }
    public Long getId() { return id; }
    public Workflow getWorkflow() { return workflow; }
    public String getInputText() { return inputText; }
    public String getOutputText() { return outputText; }
    public Instant getCreatedAt() { return createdAt; }
}

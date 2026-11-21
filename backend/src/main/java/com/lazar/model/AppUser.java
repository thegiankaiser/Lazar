package com.lazar.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_users", indexes = @Index(name = "idx_app_users_email", columnList = "email", unique = true))
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected AppUser() {}
    public AppUser(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}

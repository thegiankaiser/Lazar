package com.lazar.security;

import com.lazar.config.LazarProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtService {
    private final LazarProperties properties;
    public JwtService(LazarProperties properties) { this.properties = properties; }

    private SecretKey key() {
        String secret = properties.jwt().secret();
        if (secret == null || secret.isBlank()) throw new IllegalStateException("JWT_SECRET must be configured");
        try {
            byte[] bytes = Base64.getDecoder().decode(secret);
            if (bytes.length < 32) throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes");
            return Keys.hmacShaKeyFor(bytes);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("JWT_SECRET must be valid Base64", ex);
        }
    }
    public String generate(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(email).issuedAt(new Date(now))
            .expiration(new Date(now + properties.jwt().expirationMs()))
            .signWith(key()).compact();
    }
    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public long expirationMs() { return properties.jwt().expirationMs(); }
}

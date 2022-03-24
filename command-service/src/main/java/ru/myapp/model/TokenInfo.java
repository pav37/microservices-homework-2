package ru.myapp.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

@Getter
@Setter
public class TokenInfo {
    private String value;
    private String issuer;
    private Instant expiresAt;

    public TokenInfo(Jwt token) {
        this.value = token.getTokenValue();
        this.issuer = token.getIssuer().toString();
        this.expiresAt = token.getExpiresAt();
    }
}

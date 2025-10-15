package me.shinsunyoung.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class JwtFactory {

    private String subject = "test@email.com";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = new HashMap<>();

    public static JwtFactory builder() {
        return new JwtFactory();
    }

    public static JwtFactory withDefaultValues() {
        return new JwtFactory();
    }

    public JwtFactory subject(String subject) {
        this.subject = subject;
        return this;
    }

    public JwtFactory expiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }

    public JwtFactory claims(Map<String, Object> claims) {
        this.claims = claims;
        return this;
    }

    public JwtFactory build() {
        return this;
    }

    public String createToken(JwtProperties jwtProperties) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
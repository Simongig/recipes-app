package com.simongig.recipesapp.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

/**
 * Issues and checks the two kinds of JWT the app uses. Both are signed with the same key, so
 * the {@code token_type} claim is what keeps them apart: only access tokens are accepted as
 * Bearer credentials (see SecurityConfig's JwtDecoder), only refresh tokens can be exchanged
 * for new tokens.
 */
@Service
public class JwtTokenService {

    public static final String TOKEN_TYPE_CLAIM = "token_type";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
    public static final String ROLES_CLAIM = "roles";

    static final Duration ACCESS_TOKEN_LIFETIME = Duration.ofMinutes(15);
    static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(30);

    // HS256 needs a key of at least 256 bits; a shorter secret can be brute-forced offline
    // from any issued token.
    private static final int MIN_SECRET_BYTES = 32;

    private final byte[] secret;
    private final Algorithm algorithm;

    public JwtTokenService(@Value("${jwt.secret}") String jwtSecret) {
        this.secret = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secret.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("jwt.secret (JWT_SECRET) must be at least " + MIN_SECRET_BYTES
                    + " bytes long, e.g. generated with: openssl rand -base64 32");
        }
        this.algorithm = Algorithm.HMAC256(secret);
    }

    /** Key for verifying tokens with Spring's resource server (same secret, same algorithm). */
    public SecretKey secretKey() {
        return new SecretKeySpec(secret, "HmacSHA256");
    }

    /** A fresh access + refresh token pair in the JSON shape the frontend expects. */
    public Map<String, String> issueTokens(String username, Collection<String> roles) {
        String accessToken = JWT.create()
                .withSubject(username)
                .withClaim(TOKEN_TYPE_CLAIM, ACCESS)
                .withClaim(ROLES_CLAIM, roles.stream().toList())
                .withExpiresAt(expiresIn(ACCESS_TOKEN_LIFETIME))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(username)
                .withClaim(TOKEN_TYPE_CLAIM, REFRESH)
                .withExpiresAt(expiresIn(REFRESH_TOKEN_LIFETIME))
                .sign(algorithm);

        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    /**
     * @return the username a valid, unexpired refresh token was issued for
     * @throws JWTVerificationException for anything else, including access tokens
     */
    public String verifyRefreshToken(String token) {
        return JWT.require(algorithm)
                .withClaim(TOKEN_TYPE_CLAIM, REFRESH)
                .build()
                .verify(token)
                .getSubject();
    }

    private static Date expiresIn(Duration lifetime) {
        return new Date(System.currentTimeMillis() + lifetime.toMillis());
    }
}

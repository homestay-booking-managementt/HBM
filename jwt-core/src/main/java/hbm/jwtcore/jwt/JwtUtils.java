package hbm.jwtcore.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
@NoArgsConstructor
public class JwtUtils implements JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessTokenExpiration}")
    private int accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private int refreshTokenExpiration;


    private Key getSigningKey() {
        log.info("Secret key: {}", secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    @Override
    public List<String> extractRoles(String token) {
        List<?> rawList = extractAllClaims(token).get("roles", List.class);
        return rawList.stream()
                .map(Object::toString)
                .toList();
    }


    @Override
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String generateToken(String email, Long userId, List<String> roles) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(accessTokenExpiration, ChronoUnit.MINUTES)))
                .claim("userId", userId)
                .claim("roles", roles.toArray(new String[0]))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(String email, Long userId, List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(refreshTokenExpiration, ChronoUnit.DAYS)))
                .claim("userId", userId)
                .claim("roles", roles.toArray(new String[0]))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}


package hbm.jwtcore.jwt;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public interface JwtService {
    public String extractUsername(String token);

    public Date extractExpiration(String token);

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    boolean validateToken(String token);

    Claims extractAllClaims(String token);

    List<String> extractRoles(String token);

    Long extractUserId(String token);

    String generateToken(String email, Long userId, List<String> roles);

    String generateRefreshToken(String email, Long userId, List<String> roles);
}

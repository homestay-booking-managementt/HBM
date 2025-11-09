package hbm.authservice.service.impl;

import hbm.authservice.dto.*;
import hbm.authservice.entity.Role;
import hbm.authservice.entity.User;
import hbm.authservice.entity.UserRole;
import hbm.authservice.entity.UserSession;
import hbm.authservice.repository.RoleRepository;
import hbm.authservice.repository.UserRepository;
import hbm.authservice.repository.UserRoleRepository;
import hbm.authservice.repository.UserSessionRepository;
import hbm.authservice.service.AuthService;
import hbm.jwtcore.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // từ jwt-core

    @Transactional
    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswd(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus((byte) 1);
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // assign default role (USER)
        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        UserRole ur = new UserRole(user.getId(), role.getId());
        userRoleRepository.save(ur);

        return user;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswd())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        List<String> roles = roleRepository.findByUserId(user.getId());

        String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), roles);

        // save refresh token
        UserSession session = new UserSession();
        session.setUserId(user.getId());
        session.setRefreshToken(refreshToken);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserSession session = userSessionRepository.findByRefreshToken((refreshToken))
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = roleRepository.findByUserId(user.getId());

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), roles);
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), roles);

        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        // 5️⃣ Trả về response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        userSessionRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public TokenResponse validateToken(TokenValidateRequest request) {
        // Chỉ cần try-catch cho các loại lỗi cụ thể mà bạn muốn map thành 401
        try {
            Claims claims = jwtService.extractAllClaims(request.token());

            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            return new TokenResponse(userId, email, roles);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }
}

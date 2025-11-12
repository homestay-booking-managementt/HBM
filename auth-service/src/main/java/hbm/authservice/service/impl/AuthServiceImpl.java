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
        // Xác định role và status dựa trên roleType
        final String roleType;
        if (request.getRoleType() != null && "HOST".equalsIgnoreCase(request.getRoleType())) {
            // Đăng ký HOST: status = 0 (chưa kích hoạt), chờ admin duyệt
            user.setStatus((byte) 0);
            roleType = "HOST";
        } else {
            // Đăng ký CUSTOMER: status = 1 (hoạt động), không cần duyệt
            user.setStatus((byte) 1);
            roleType = "CUSTOMER";
        }
        
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Gán role
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType));
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

        // Kiểm tra trạng thái tài khoản
        if (user.getStatus() == 0) {
            throw new RuntimeException("Account is pending approval");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("Account is temporarily suspended");
        }
        if (user.getStatus() == 3) {
            throw new RuntimeException("Account is blocked");
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
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .roles(roles)
                        .status(user.getStatus())
                        .build())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserSession session = userSessionRepository.findByRefreshToken((refreshToken))
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        Long userId = session.getUserId();
        if (userId == null) {
            throw new RuntimeException("Invalid session");
        }

        User user = userRepository.findById(userId)
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
        try {
            Claims claims = jwtService.extractAllClaims(request.token());

            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            return new TokenResponse(userId, email, roles);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }

    @Override
    public AuthResponse.UserInfo getCurrentUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
        }

        String token = authorization.substring(7);
        
        try {
            Claims claims = jwtService.extractAllClaims(token);

            Long userId = claims.get("userId", Long.class);
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: missing userId");
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return AuthResponse.UserInfo.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .roles(roles)
                    .status(user.getStatus())
                    .build();
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }
}

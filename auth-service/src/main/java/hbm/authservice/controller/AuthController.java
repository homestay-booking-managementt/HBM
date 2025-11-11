package hbm.authservice.controller;

import hbm.authservice.dto.*;
import hbm.authservice.entity.User;
import hbm.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/v1/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/v1/refresh-token")
    public AuthResponse postRefreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/v1/logout")
    public void logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
    }

    @PostMapping("/v1/validate")
    public TokenResponse validateToken(@RequestBody TokenValidateRequest request) {
        return authService.validateToken(request);
    }

    @GetMapping("/v1/me")
    public AuthResponse.UserInfo getCurrentUser(@RequestHeader("Authorization") String authorization) {
        return authService.getCurrentUser(authorization);
    }
}

package hbm.authservice.service;

import hbm.authservice.dto.*;
import hbm.authservice.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    TokenResponse validateToken(TokenValidateRequest request);
}

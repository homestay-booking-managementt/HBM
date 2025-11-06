package hbm.authservice.dto;

import java.util.List;

public record TokenResponse(
    Long userId,
    String email,
    List<String> roles
) { }

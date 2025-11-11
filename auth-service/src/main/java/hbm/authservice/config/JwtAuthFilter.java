package hbm.authservice.config;

import hbm.authservice.service.impl.CustomUserDetails;
import hbm.authservice.service.impl.CustomUserDetailsService;
import hbm.jwtcore.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Giả định bạn có một JwtService để xử lý token (validate, parse claims)

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Service bạn dùng để validate và parse JWT
    private final CustomUserDetailsService userDetailsService; // Service để tải UserDetails nếu cần

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy Authorization Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Trích xuất JWT
        jwt = authHeader.substring(7);

        // 3. Trích xuất email (hoặc user ID) từ JWT (Không cần gọi DB tại đây)
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Validate Token
            if (jwtService.validateToken(jwt)) {

                // 5. Trích xuất Claims (ID, Roles) từ JWT
                // Nếu bạn lưu userId và roles trong JWT, bạn có thể tạo CustomUserDetails trực tiếp.

                Long userId = jwtService.extractUserId(jwt);
                List<String> rolesList = jwtService.extractRoles(jwt); // Giả định hàm này parse Role claims

                List<SimpleGrantedAuthority> authorities = rolesList.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                        .toList();

                // Tạo CustomUserDetails (Giống như logic trong Gateway filter cũ)
                CustomUserDetails userDetails = new CustomUserDetails(userEmail, userId, rolesList);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                // 6. Thiết lập Authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

package hbm.authservice.config;

import hbm.authservice.service.impl.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class GatewayAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_SERVICE_PREFIX = "/auth/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith(AUTH_SERVICE_PREFIX)) {
            log.info("Bypassing JWT filter for Auth service path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = request.getHeader("X-User-Id");
        final String email = request.getHeader("X-User-Email");
        final String roles = request.getHeader("X-User-Roles");

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long id = Long.parseLong(userId);
            List<String> rolesList = Arrays.stream(roles.split(","))
                    .filter(role -> !role.trim().isEmpty())
                    .toList();

            List<SimpleGrantedAuthority> authorities = rolesList.stream()
                    .filter(role -> !role.trim().isEmpty())
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                    .toList();

            CustomUserDetails userDetails = new CustomUserDetails(email, id, rolesList);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // Password là null vì đã xác thực ở Gateway
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

//package hbm.authservice.config;
//
//import hbm.authservice.service.impl.CustomUserDetailsService;
//import hbm.jwtcore.jwt.JwtService;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final CustomUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        log.info("JwtAuthFilter: {}", request.getRequestURI());
//
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String username;
//        List<String> roles;
//
//        // 1️⃣ Bỏ qua nếu không có header Authorization
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            log.info("No JWT, skipping filter for {}", request.getRequestURI());
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7); // bỏ "Bearer "
//        try {
//            username = jwtService.extractUsername(jwt);
//            roles = jwtService.extractRoles(jwt);
//            roles.forEach(role -> log.info("Role: {}", role));
//        } catch (ExpiredJwtException e) {
//            log.warn("JWT expired");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"error\":\"Token expired\"}");
//            response.setContentType("application/json");
//            return;
//        } catch (Exception e) {
//            log.error("Invalid JWT");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"error\":\"Invalid token\"}");
//            response.setContentType("application/json");
//            return;
//        }
//
//        // 2️⃣ Nếu đã có authentication thì bỏ qua
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            List<SimpleGrantedAuthority> authorities = roles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .toList();
//
//            if (jwtService.validateToken(jwt)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                authorities
//                        );
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // ✅ Gắn vào context để @PreAuthorize hoạt động
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/auth/v1/login")
//                || path.startsWith("/auth/v1/register")
//                || path.startsWith("/auth/v1/refresh-token")
//                || path.startsWith("/auth/v1/validate"); // ✅ Validate token API phải bypass filter
//    }
//}

package hbm.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;
    private static final String AUTH_SERVICE_PREFIX = "/auth/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 1. KIỂM TRA ĐƯỜNG DẪN (SHOULD NOT FILTER)
        if (path.startsWith(AUTH_SERVICE_PREFIX)) {
            log.info("Bypassing JWT filter for Auth service path: {}", path);
            // ✅ Tiếp tục chuỗi filter mà không kiểm tra JWT
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/auth/v1/validate")
                .bodyValue(new TokenValidateRequest(token))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .flatMap(res -> {
                    log.info("✅ JWT verified. userId={}, roles={}", res.userId(), res.roles());

                    ServerHttpRequest requestWithHeaders = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(res.userId()))
                            .header("X-User-Email", res.email())
                            .header("X-User-Roles", String.join(",", res.roles()))
                            .build();

                    // 2. TẠO SERVERWEBEXCHANGE MỚI VỚI REQUEST ĐÃ CẬP NHẬT
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(requestWithHeaders)
                            .build();

                    // 3. CHUYỂN TIẾP EXCHANGE MỚI
                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(e -> unauthorized(exchange, "JWT validation failed"));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.warn("❌ {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    record TokenValidateRequest(String token){}
    record TokenResponse(Long userId, String email, List<String> roles){}
}

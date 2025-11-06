package hbm.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoggingFilter {
    @Bean
    public GlobalFilter logFilter() {
        return (exchange, chain) -> {
            log.info(">>> Incoming request path: {}", exchange.getRequest().getURI());
            log.info(">>> AUTH HEADER: {}", exchange.getRequest().getHeaders().getFirst("Authorization"));
            return chain.filter(exchange);
        };
    }
}


package com.capg.smartcourier.filter;

import com.capg.smartcourier.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Allow auth endpoints
        if (path.contains("/api/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // ✅ pass username, userId and roles to downstream services
        String username = jwtUtil.extractUsername(token);
        Long userId = jwtUtil.extractUserId(token);
        java.util.List<String> roles = jwtUtil.extractRoles(token);
        
        String rolesStr = (roles != null) ? String.join(",", roles) : "";

        // 🛡️ Admin Access Control
        if (path.contains("/api/admin") && (roles == null || !roles.contains("ROLE_ADMIN"))) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User", username)
                        .header("X-User-Id", userId.toString())
                        .header("X-Roles", rolesStr)
                        .build())
                .build();

        return chain.filter(mutatedExchange);
    }
}
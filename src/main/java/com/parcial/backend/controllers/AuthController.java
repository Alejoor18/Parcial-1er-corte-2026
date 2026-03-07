package com.parcial.backend.controllers;

import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import infrastructure.security.JwtService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(ReactiveUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> login(@RequestBody Mono<AuthRequest> body) {
        return body.flatMap(req -> {
            if (req == null || isBlank(req.username) || isBlank(req.password)) {
                return Mono.just(ResponseEntity.badRequest().build());
            }

            return userDetailsService.findByUsername(req.username)
                    .flatMap(user -> {
                        if (!passwordEncoder.matches(req.password, user.getPassword())) {
                            return Mono.just(ResponseEntity.status(401).build());
                        }

                        List<String> tokenRoles = user.getAuthorities().stream()
                                .map(a -> a.getAuthority())
                                .collect(Collectors.toList());

                        String token = jwtService.generateToken(user.getUsername(), tokenRoles);

                        List<String> roles = user.getAuthorities().stream()
                                .map(a -> normalizeRole(a.getAuthority()))
                                .collect(Collectors.toList());

                        return Mono.just(ResponseEntity.ok(new AuthResponse(token, roles)));
                    })
                    .switchIfEmpty(Mono.just(ResponseEntity.status(401).build()));
        });
    }

    private String normalizeRole(@Nullable String authority) {
        if (authority == null || authority.isBlank()) {
            return "";
        }
        return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static class AuthRequest {
        public String username;
        public String password;

        public AuthRequest() {
        }

        public AuthRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class AuthResponse {
        public String token;
        public List<String> roles;

        public AuthResponse() {
        }

        public AuthResponse(String token, List<String> roles) {
            this.token = token;
            this.roles = roles;
        }
    }
}
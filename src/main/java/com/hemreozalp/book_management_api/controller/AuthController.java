package com.hemreozalp.book_management_api.controller;

import com.hemreozalp.book_management_api.model.User;
import com.hemreozalp.book_management_api.service.AuthService;
import com.hemreozalp.book_management_api.util.JwtServcice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtServcice jwtService;

    public AuthController(AuthService authService,
                          JwtServcice jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String password = body.get("password");
        User user = authService.register(username, password);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String password = body.get("password");

        return authService.authenticate(username, password)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}

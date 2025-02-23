package com.financialsystem.controller;

import com.financialsystem.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/client")
    public Long registerClient(@RequestParam String username, @RequestParam String password) {
        Long id = authService.registerClient(username, password);
        return id;
    }
}

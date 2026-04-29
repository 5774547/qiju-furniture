package com.qiju.furniture.module.auth.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.auth.dto.AuthResponse;
import com.qiju.furniture.module.auth.dto.LoginDTO;
import com.qiju.furniture.module.auth.dto.RegisterDTO;
import com.qiju.furniture.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller - registration and login
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     *
     * @param registerDTO Registration data
     * @return Auth response with token and user info
     */
    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("User registration request: username={}", registerDTO.getUsername());
        AuthResponse response = authService.register(registerDTO);
        return Result.ok(response);
    }

    /**
     * Login with username and password
     *
     * @param loginDTO Login credentials
     * @return Auth response with token and user info
     */
    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("User login request: username={}", loginDTO.getUsername());
        AuthResponse response = authService.login(loginDTO);
        return Result.ok(response);
    }
}

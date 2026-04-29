package com.qiju.furniture.module.auth.service;

import com.qiju.furniture.module.auth.dto.AuthResponse;
import com.qiju.furniture.module.auth.dto.LoginDTO;
import com.qiju.furniture.module.auth.dto.RegisterDTO;

/**
 * Auth Service Interface
 *
 * @author Qiju Team
 */
public interface AuthService {

    /**
     * Register a new user
     *
     * @param registerDTO Registration data
     * @return Auth response with token and user info
     */
    AuthResponse register(RegisterDTO registerDTO);

    /**
     * Login with username and password
     *
     * @param loginDTO Login credentials
     * @return Auth response with token and user info
     */
    AuthResponse login(LoginDTO loginDTO);
}

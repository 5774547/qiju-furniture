package com.qiju.furniture.module.auth.service.impl;

import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.common.security.JwtUtil;
import com.qiju.furniture.module.auth.dto.AuthResponse;
import com.qiju.furniture.module.auth.dto.LoginDTO;
import com.qiju.furniture.module.auth.dto.RegisterDTO;
import com.qiju.furniture.module.auth.service.AuthService;
import com.qiju.furniture.module.user.dto.UserVO;
import com.qiju.furniture.module.user.entity.User;
import com.qiju.furniture.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Auth Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterDTO registerDTO) {
        // Check if username already exists
        if (userService.findByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(400, "Username already exists");
        }

        // Build user entity
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(registerDTO.getPassword())
                .email(registerDTO.getEmail())
                .nickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername())
                .role("user")
                .status(1)
                .build();

        // Register user (password will be hashed inside userService.register)
        userService.register(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole() != null ? user.getRole() : "user");

        // Build response
        UserVO userVO = convertToVO(user);
        log.info("User registered successfully: username={}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(userVO)
                .build();
    }

    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        // Find user by username
        User user = userService.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(401, "Invalid username or password");
        }

        // Check if user is disabled
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "Account has been disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole() != null ? user.getRole() : "user");

        // Build response
        UserVO userVO = convertToVO(user);
        log.info("User logged in: username={}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(userVO)
                .build();
    }

    /**
     * Convert User entity to UserVO (strip sensitive fields)
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}

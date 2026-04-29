package com.qiju.furniture.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.module.user.entity.User;
import com.qiju.furniture.module.user.mapper.UserMapper;
import com.qiju.furniture.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return this.getOne(wrapper);
    }

    @Override
    public User register(User user) {
        // Check if username already exists
        if (findByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "Username already exists");
        }

        // Check if email already exists
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            User existingEmail = findByEmail(user.getEmail());
            if (existingEmail != null) {
                throw new BusinessException(400, "Email already exists");
            }
        }

        // Hash password with BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set defaults
        if (user.getRole() == null) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        this.save(user);
        log.info("User registered: username={}, email={}", user.getUsername(), user.getEmail());
        return user;
    }

    @Override
    public User updateProfile(Long userId, User updated) {
        User existing = this.getById(userId);
        if (existing == null) {
            throw new BusinessException(404, "User not found");
        }

        // Only allow updating certain fields
        if (updated.getNickname() != null) {
            existing.setNickname(updated.getNickname());
        }
        if (updated.getPhone() != null) {
            existing.setPhone(updated.getPhone());
        }
        if (updated.getAvatar() != null) {
            existing.setAvatar(updated.getAvatar());
        }
        if (updated.getEmail() != null && !updated.getEmail().equals(existing.getEmail())) {
            // Check email uniqueness
            User emailUser = findByEmail(updated.getEmail());
            if (emailUser != null && !emailUser.getId().equals(userId)) {
                throw new BusinessException(400, "Email already exists");
            }
            existing.setEmail(updated.getEmail());
        }

        this.updateById(existing);
        log.info("User profile updated: userId={}", userId);
        return existing;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User existing = this.getById(userId);
        if (existing == null) {
            throw new BusinessException(404, "User not found");
        }

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, existing.getPassword())) {
            throw new BusinessException(400, "旧密码不正确");
        }

        // Hash and set new password
        existing.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(existing);
        log.info("Password changed successfully for userId={}", userId);
    }
}

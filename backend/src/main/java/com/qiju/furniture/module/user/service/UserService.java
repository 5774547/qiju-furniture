package com.qiju.furniture.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.user.entity.User;

/**
 * User Service Interface
 *
 * @author Qiju Team
 */
public interface UserService extends IService<User> {

    /**
     * Find user by username
     *
     * @param username Username
     * @return User or null
     */
    User findByUsername(String username);

    /**
     * Find user by email
     *
     * @param email Email
     * @return User or null
     */
    User findByEmail(String email);

    /**
     * Register a new user
     *
     * @param user User entity with raw password
     * @return Created user
     */
    User register(User user);

    /**
     * Update user profile
     *
     * @param userId   User ID
     * @param updated  Updated user fields
     * @return Updated user
     */
    User updateProfile(Long userId, User updated);

    /**
     * Change user password
     *
     * @param userId      User ID
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}

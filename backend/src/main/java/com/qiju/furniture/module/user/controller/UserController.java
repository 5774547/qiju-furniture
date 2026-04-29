package com.qiju.furniture.module.user.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.user.dto.PasswordChangeDTO;
import com.qiju.furniture.module.user.dto.UserUpdateDTO;
import com.qiju.furniture.module.user.dto.UserVO;
import com.qiju.furniture.module.user.entity.User;
import com.qiju.furniture.module.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller - profile management
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user profile
     *
     * @param authentication Spring Security authentication
     * @return User profile
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "User not found");
        }
        UserVO userVO = convertToVO(user);
        return Result.ok(userVO);
    }

    /**
     * Update user profile
     *
     * @param authentication Spring Security authentication
     * @param updated         Updated user fields
     * @return Updated user profile
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(Authentication authentication,
                                         @Valid @RequestBody UserUpdateDTO updated) {
        Long userId = getCurrentUserId(authentication);
        // Map UserUpdateDTO to User entity
        User userEntity = new User();
        BeanUtils.copyProperties(updated, userEntity);
        User user = userService.updateProfile(userId, userEntity);
        UserVO userVO = convertToVO(user);
        return Result.ok(userVO);
    }

    /**
     * Change password
     *
     * @param authentication Spring Security authentication
     * @param passwordChange  Old and new password
     * @return Success message
     */
    @PutMapping("/password")
    public Result<Void> changePassword(Authentication authentication,
                                        @Valid @RequestBody PasswordChangeDTO passwordChange) {
        Long userId = getCurrentUserId(authentication);
        userService.changePassword(userId, passwordChange.getOldPassword(), passwordChange.getNewPassword());
        return Result.ok(null);
    }

    /**
     * Extract current user ID from authentication
     *
     * @param authentication Spring Security authentication
     * @return User ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (Long) authentication.getPrincipal();
    }

    /**
     * Convert User entity to UserVO (strip password)
     *
     * @param user User entity
     * @return UserVO
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

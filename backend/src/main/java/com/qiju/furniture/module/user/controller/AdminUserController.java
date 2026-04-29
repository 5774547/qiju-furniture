package com.qiju.furniture.module.user.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.user.entity.User;
import com.qiju.furniture.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台用户管理 Controller
 */
@Tag(name = "后台用户管理")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取所有用户")
    @GetMapping
    public Result<List<UserVO>> listAllUsers() {
        List<User> users = userService.list();
        List<UserVO> voList = users.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.ok(voList);
    }

    @Operation(summary = "切换用户状态(禁用/启用)")
    @PutMapping("/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userService.updateById(user);
        return Result.ok();
    }

    private UserVO convertToVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    // Inner DTO to avoid password exposure
    static class UserVO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String nickname;
        private String avatar;
        private String role;
        private Integer status;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public java.time.LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(java.time.LocalDateTime createTime) { this.createTime = createTime; }
        public java.time.LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(java.time.LocalDateTime updateTime) { this.updateTime = updateTime; }
    }
}

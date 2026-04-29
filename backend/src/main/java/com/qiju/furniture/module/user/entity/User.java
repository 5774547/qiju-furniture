package com.qiju.furniture.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Username (unique)
     */
    private String username;

    /**
     * Password (BCrypt hashed)
     */
    private String password;

    /**
     * Email (unique)
     */
    private String email;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Avatar URL
     */
    private String avatar;

    /**
     * Role: user / admin
     */
    @Builder.Default
    private String role = "user";

    /**
     * Status: 0=disabled, 1=enabled
     */
    @Builder.Default
    private Integer status = 1;

    /**
     * Creation time
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

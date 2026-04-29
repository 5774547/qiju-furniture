package com.qiju.furniture.module.contact.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Contact Form Submission Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("contact")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Sender name
     */
    private String name;

    /**
     * Sender email
     */
    private String email;

    /**
     * Sender phone (optional)
     */
    private String phone;

    /**
     * Subject
     */
    private String subject;

    /**
     * Message content
     */
    private String message;

    /**
     * Status (0-unread, 1-read, 2-replied)
     */
    private Integer status;

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

package com.qiju.furniture.module.newsletter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Newsletter Subscription Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("newsletter")
public class Newsletter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Subscriber email
     */
    private String email;

    /**
     * Subscription status (0-unsubscribed, 1-subscribed)
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

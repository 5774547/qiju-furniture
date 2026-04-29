package com.qiju.furniture.module.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Review Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Associated product ID
     */
    private Long productId;

    /**
     * Reviewer name
     */
    private String reviewerName;

    /**
     * Review rating (1-5)
     */
    private Integer rating;

    /**
     * Review content
     */
    private String content;

    /**
     * Reviewer email (optional)
     */
    private String email;

    /**
     * Review status (0-pending, 1-approved, 2-rejected)
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

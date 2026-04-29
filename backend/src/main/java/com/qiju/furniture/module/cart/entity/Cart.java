package com.qiju.furniture.module.cart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cart Item Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Session ID (anonymous identification)
     */
    private String sessionId;

    /**
     * User ID (for logged-in users)
     */
    private Long userId;

    /**
     * Product ID
     */
    private Long productId;

    /**
     * Product name (snapshot)
     */
    private String productName;

    /**
     * Product image (snapshot)
     */
    private String productImage;

    /**
     * Unit price (snapshot)
     */
    private BigDecimal price;

    /**
     * Quantity
     */
    private Integer quantity;

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

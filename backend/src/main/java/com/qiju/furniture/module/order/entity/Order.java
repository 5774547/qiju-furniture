package com.qiju.furniture.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Order number
     */
    private String orderNo;

    /**
     * User ID (for logged-in users)
     */
    private Long userId;

    /**
     * Session ID (anonymous identification)
     */
    private String sessionId;

    /**
     * Customer name
     */
    private String customerName;

    /**
     * Customer email
     */
    private String customerEmail;

    /**
     * Customer phone
     */
    private String customerPhone;

    /**
     * Shipping address
     */
    private String address;

    /**
     * Total amount
     */
    private BigDecimal totalAmount;

    /**
     * Discount amount
     */
    private BigDecimal discountAmount;

    /**
     * Final amount to pay
     */
    private BigDecimal finalAmount;

    /**
     * Coupon code used (if any)
     */
    private String couponCode;

    /**
     * Order status (0-pending, 1-paid, 2-shipped, 3-delivered, 4-cancelled)
     */
    private Integer status;

    /**
     * Customer notes
     */
    private String notes;

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

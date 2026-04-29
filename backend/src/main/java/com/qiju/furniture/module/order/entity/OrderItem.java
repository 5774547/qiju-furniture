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
 * Order Item Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Associated order ID
     */
    private Long orderId;

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
     * Subtotal
     */
    private BigDecimal subtotal;

    /**
     * Creation time
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

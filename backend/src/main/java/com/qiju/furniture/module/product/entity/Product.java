package com.qiju.furniture.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Product Entity
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Product name
     */
    private String name;

    /**
     * Product category
     */
    private String category;

    /**
     * Short description
     */
    private String description;

    /**
     * Detailed description (rich text)
     */
    private String detail;

    /**
     * Current price
     */
    private BigDecimal price;

    /**
     * Original price (for discount display)
     */
    private BigDecimal originalPrice;

    /**
     * Product tag (e.g., "Hot", "New", "Sale")
     */
    private String tag;

    /**
     * Main product image URL
     */
    private String image;

    /**
     * Additional images JSON array string
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String images;

    /**
     * Product specifications JSON string
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String specs;

    /**
     * Average rating (1-5)
     */
    private BigDecimal rating;

    /**
     * Number of reviews
     */
    private Integer reviewCount;

    /**
     * Whether the product is in stock
     */
    private Boolean inStock;

    /**
     * Current stock count
     */
    private Integer stockCount;

    /**
     * Estimated next restock date
     */
    private LocalDate nextBatch;

    /**
     * Sort order (for display ordering)
     */
    private Integer sort;

    /**
     * Product status (0-disabled, 1-enabled)
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

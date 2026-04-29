package com.qiju.furniture.module.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cart Add Data Transfer Object
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;

    private Long userId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}

package com.qiju.furniture.module.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Order Create Data Transfer Object
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotBlank(message = "Address is required")
    private String address;

    /**
     * Coupon code (optional)
     */
    private String couponCode;

    /**
     * Customer notes (optional)
     */
    private String notes;

    @NotEmpty(message = "Order items are required")
    private List<OrderItemDTO> items;

    /**
     * Order Item DTO (inner)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotBlank(message = "Product name is required")
        private String productName;

        private String productImage;

        @NotNull(message = "Price is required")
        private BigDecimal price;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}

package com.qiju.furniture.module.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Product View Object
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String category;
    private String description;
    private String detail;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String tag;
    private String image;
    private List<String> images;
    private Map<String, String> specs;
    private BigDecimal rating;
    private Integer reviewCount;
    private Boolean inStock;
    private Integer stockCount;
}

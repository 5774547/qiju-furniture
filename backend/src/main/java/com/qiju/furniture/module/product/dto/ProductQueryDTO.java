package com.qiju.furniture.module.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Product Query Data Transfer Object
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Filter by category
     */
    private String category;

    /**
     * Search keyword (matches name and description)
     */
    private String keyword;

    /**
     * Minimum price filter
     */
    private BigDecimal priceMin;

    /**
     * Maximum price filter
     */
    private BigDecimal priceMax;

    /**
     * Sort field (price_asc, price_desc, rating, newest)
     */
    private String sortBy;

    /**
     * Filter by tag
     */
    private String tag;

    /**
     * Product IDs for "liked" filter
     */
    private List<Long> likedIds;
}

package com.qiju.furniture.module.product.controller;

import com.qiju.furniture.common.result.PageResult;
import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.product.dto.ProductQueryDTO;
import com.qiju.furniture.module.product.dto.ProductVO;
import com.qiju.furniture.module.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product Controller
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * List products with filters, sorting and pagination
     */
    @GetMapping
    public Result<PageResult<ProductVO>> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String priceMin,
            @RequestParam(required = false) String priceMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        ProductQueryDTO queryDTO = ProductQueryDTO.builder()
                .category(category)
                .keyword(keyword)
                .sortBy(sortBy)
                .tag(tag)
                .build();

        if (priceMin != null && !priceMin.isEmpty()) {
            queryDTO.setPriceMin(new java.math.BigDecimal(priceMin));
        }
        if (priceMax != null && !priceMax.isEmpty()) {
            queryDTO.setPriceMax(new java.math.BigDecimal(priceMax));
        }

        PageResult<ProductVO> result = productService.listProducts(queryDTO, page, size);
        return Result.ok(result);
    }

    /**
     * Get product detail by ID
     *
     * @param id Product ID
     * @return Product detail
     */
    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO product = productService.getProductDetail(id);
        return Result.ok(product);
    }

    /**
     * List all categories with product counts
     *
     * @return List of categories with counts
     */
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> listCategories() {
        List<Map<String, Object>> categories = productService.listCategoriesWithCount();
        return Result.ok(categories);
    }
}

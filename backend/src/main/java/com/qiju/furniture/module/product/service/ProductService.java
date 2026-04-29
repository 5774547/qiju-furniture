package com.qiju.furniture.module.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.common.result.PageResult;
import com.qiju.furniture.module.product.dto.ProductQueryDTO;
import com.qiju.furniture.module.product.dto.ProductVO;
import com.qiju.furniture.module.product.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * Product Service Interface
 */
public interface ProductService extends IService<Product> {

    /**
     * Query products with filters, sorting and pagination
     */
    PageResult<ProductVO> listProducts(ProductQueryDTO queryDTO, int page, int size);

    /**
     * Get product detail by ID
     */
    ProductVO getProductDetail(Long id);

    /**
     * Get all categories with product counts
     */
    List<Map<String, Object>> listCategoriesWithCount();
}

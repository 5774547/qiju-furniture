package com.qiju.furniture.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.common.result.PageResult;
import com.qiju.furniture.module.product.dto.ProductQueryDTO;
import com.qiju.furniture.module.product.dto.ProductVO;
import com.qiju.furniture.module.product.entity.Product;
import com.qiju.furniture.module.product.mapper.ProductMapper;
import com.qiju.furniture.module.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Product Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResult<ProductVO> listProducts(ProductQueryDTO queryDTO, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        // Filter by category
        if (StringUtils.isNotBlank(queryDTO.getCategory())) {
            wrapper.eq(Product::getCategory, queryDTO.getCategory());
        }

        // Filter by tag
        if (StringUtils.isNotBlank(queryDTO.getTag())) {
            wrapper.eq(Product::getTag, queryDTO.getTag());
        }

        // Search by keyword in name or description
        if (StringUtils.isNotBlank(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword().trim();
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword));
        }

        // Filter by price range
        if (queryDTO.getPriceMin() != null) {
            wrapper.ge(Product::getPrice, queryDTO.getPriceMin());
        }
        if (queryDTO.getPriceMax() != null) {
            wrapper.le(Product::getPrice, queryDTO.getPriceMax());
        }

        // Filter by liked IDs
        if (queryDTO.getLikedIds() != null && !queryDTO.getLikedIds().isEmpty()) {
            wrapper.in(Product::getId, queryDTO.getLikedIds());
        }

        // Sorting
        if (StringUtils.isNotBlank(queryDTO.getSortBy())) {
            switch (queryDTO.getSortBy()) {
                case "price_asc":
                    wrapper.orderByAsc(Product::getPrice);
                    break;
                case "price_desc":
                    wrapper.orderByDesc(Product::getPrice);
                    break;
                case "rating":
                    wrapper.orderByDesc(Product::getRating);
                    break;
                case "newest":
                    wrapper.orderByDesc(Product::getCreateTime);
                    break;
                default:
                    wrapper.orderByAsc(Product::getSort);
                    break;
            }
        } else {
            wrapper.orderByAsc(Product::getSort);
        }

        // Pagination
        IPage<Product> ipage = this.page(new Page<>(page, size), wrapper);
        List<ProductVO> voList = ipage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());

        return PageResult.of(voList, ipage.getTotal(), page, size);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException(404, "Product not found");
        }
        return convertToVO(product);
    }

    @Override
    public List<Map<String, Object>> listCategoriesWithCount() {
        return baseMapper.selectCategoriesWithCount();
    }

    /**
     * Convert Product entity to ProductVO, parsing JSON fields
     */
    private ProductVO convertToVO(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // Parse images JSON string to List<String>
        if (StringUtils.isNotBlank(product.getImages())) {
            try {
                List<String> imagesList = objectMapper.readValue(product.getImages(),
                        new TypeReference<List<String>>() {});
                vo.setImages(imagesList);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse images JSON for product {}: {}", product.getId(), e.getMessage());
                vo.setImages(Collections.emptyList());
            }
        } else {
            vo.setImages(Collections.emptyList());
        }

        // Parse specs JSON string to Map<String, String>
        if (StringUtils.isNotBlank(product.getSpecs())) {
            try {
                Map<String, String> specsMap = objectMapper.readValue(product.getSpecs(),
                        new TypeReference<Map<String, String>>() {});
                vo.setSpecs(specsMap);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse specs JSON for product {}: {}", product.getId(), e.getMessage());
                vo.setSpecs(Collections.emptyMap());
            }
        } else {
            vo.setSpecs(Collections.emptyMap());
        }

        return vo;
    }
}

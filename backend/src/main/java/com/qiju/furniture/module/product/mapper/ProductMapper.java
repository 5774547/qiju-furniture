package com.qiju.furniture.module.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiju.furniture.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Product Mapper
 *
 * @author Qiju Team
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * Get all categories with product counts
     */
    @Select("SELECT category AS name, COUNT(*) AS count FROM product WHERE status = 1 GROUP BY category ORDER BY category")
    List<Map<String, Object>> selectCategoriesWithCount();
}

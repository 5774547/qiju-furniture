package com.qiju.furniture.module.system.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.common.service.MinioService;
import com.qiju.furniture.module.product.entity.Product;
import com.qiju.furniture.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 小程序专用控制器
 * 直接返回产品图片的 base64 dataURI，一次请求加载所有数据
 */
@Tag(name = "小程序接口")
@RestController
@RequestMapping("/api/miniapp")
public class MiniAppController {

    private static final Logger log = LoggerFactory.getLogger(MiniAppController.class);

    private final ProductService productService;
    private final MinioService minioService;

    public MiniAppController(ProductService productService, MinioService minioService) {
        this.productService = productService;
        this.minioService = minioService;
    }

    @Operation(summary = "获取产品列表（含图片 base64）")
    @GetMapping("/products")
    public Result<List<Map<String, Object>>> getProductsWithImages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            var queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>();
            queryWrapper.eq(Product::getStatus, 1).orderByAsc(Product::getSort);
            var iPage = productService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), queryWrapper);

            List<Map<String, Object>> result = new ArrayList<>();
            for (Product p : iPage.getRecords()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", p.getId());
                item.put("name", p.getName());
                item.put("category", p.getCategory());
                item.put("price", p.getPrice());
                item.put("wholesalePrice", p.getWholesalePrice());
                item.put("unit", p.getUnit());
                item.put("tag", p.getTag());
                item.put("stockCount", p.getStockCount());
                item.put("rating", p.getRating());

                // 嵌入产品图片 base64
                String imageDataUri = getProductImageDataUri(p.getImage());
                item.put("imageDataUri", imageDataUri != null ? imageDataUri : "");

                result.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("records", result);
            data.put("total", iPage.getTotal());
            data.put("page", iPage.getCurrent());
            data.put("size", iPage.getSize());

            return Result.ok(data);
        } catch (Exception e) {
            log.error("Failed to get products for miniapp", e);
            return Result.error(500, "获取产品失败");
        }
    }

    @Operation(summary = "获取产品详情（含图片 base64）")
    @GetMapping("/products/{id}")
    public Result<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        try {
            Product p = productService.getById(id);
            if (p == null) {
                return Result.error(404, "产品不存在");
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("category", p.getCategory());
            item.put("description", p.getDescription());
            item.put("detail", p.getDetail());
            item.put("price", p.getPrice());
            item.put("wholesalePrice", p.getWholesalePrice());
            item.put("unit", p.getUnit());
            item.put("tag", p.getTag());
            item.put("image", p.getImage());
            item.put("specs", p.getSpecs());
            item.put("rating", p.getRating());
            item.put("stockCount", p.getStockCount());

            // 嵌入图片 base64
            String imageDataUri = getProductImageDataUri(p.getImage());
            item.put("imageDataUri", imageDataUri != null ? imageDataUri : "");

            return Result.ok(item);
        } catch (Exception e) {
            log.error("Failed to get product detail for miniapp", e);
            return Result.error(500, "获取产品详情失败");
        }
    }

    private String getProductImageDataUri(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;
        try {
            // 从 MinIO URL 提取文件名
            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            String bucket = "qiju-furniture";
            String objectName = "products/" + filename;

            if (!minioService.objectExists(bucket, objectName)) return null;

            String contentType = "image/jpeg";
            if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".gif")) contentType = "image/gif";

            try (var is = minioService.getObject(bucket, objectName)) {
                byte[] bytes = is.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                return "data:" + contentType + ";base64," + base64;
            }
        } catch (Exception e) {
            log.warn("Failed to embed image: {}", imageUrl, e);
            return null;
        }
    }
}

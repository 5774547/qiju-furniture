package com.qiju.furniture.module.product.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.product.entity.Product;
import com.qiju.furniture.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 后台商品管理 Controller (需要 admin 角色)
 */
@Tag(name = "后台商品管理")
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Product> createProduct(@Valid @RequestBody Product product) {
        product.setStatus(1);
        productService.save(product);
        return Result.ok(product);
    }

    @Operation(summary = "修改商品")
    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        Product updated = productService.getById(id);
        return Result.ok(updated);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            product.setStatus(0); // 逻辑删除
            productService.updateById(product);
        }
        return Result.ok();
    }

    @Operation(summary = "获取所有商品（含下架）")
    @GetMapping("/all")
    public Result<java.util.List<Product>> listAllProducts() {
        return Result.ok(productService.list());
    }
}

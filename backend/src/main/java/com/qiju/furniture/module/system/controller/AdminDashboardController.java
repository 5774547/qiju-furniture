package com.qiju.furniture.module.system.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.order.entity.Order;
import com.qiju.furniture.module.order.service.OrderService;
import com.qiju.furniture.module.product.service.ProductService;
import com.qiju.furniture.module.review.service.ReviewService;
import com.qiju.furniture.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台仪表盘 Controller
 */
@Tag(name = "后台仪表盘")
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final ReviewService reviewService;

    public AdminDashboardController(ProductService productService,
                                     OrderService orderService,
                                     UserService userService,
                                     ReviewService reviewService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Product counts
        long totalProducts = productService.count();
        long onlineProducts = productService.lambdaQuery()
                .eq(com.qiju.furniture.module.product.entity.Product::getStatus, 1)
                .count();
        stats.put("totalProducts", totalProducts);
        stats.put("onlineProducts", onlineProducts);

        // Order counts
        long totalOrders = orderService.count();
        long pendingOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, 0)
                .count();
        long paidOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, 1)
                .count();
        long shippedOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, 2)
                .count();
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("paidOrders", paidOrders);
        stats.put("shippedOrders", shippedOrders);

        // Revenue
        List<Order> allOrders = orderService.list();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() >= 1 && o.getStatus() <= 3) // paid/shipped/delivered
                .map(Order::getFinalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        // User counts
        long totalUsers = userService.count();
        stats.put("totalUsers", totalUsers);

        // Review counts
        long totalReviews = reviewService.count();
        stats.put("totalReviews", totalReviews);

        return Result.ok(stats);
    }
}

package com.qiju.furniture.module.system.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.inquiry.entity.Inquiry;
import com.qiju.furniture.module.inquiry.service.InquiryService;
import com.qiju.furniture.module.product.service.ProductService;
import com.qiju.furniture.module.review.service.ReviewService;
import com.qiju.furniture.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台仪表盘 Controller
 */
@Tag(name = "后台仪表盘")
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final ProductService productService;
    private final InquiryService inquiryService;
    private final UserService userService;
    private final ReviewService reviewService;

    public AdminDashboardController(ProductService productService,
                                     InquiryService inquiryService,
                                     UserService userService,
                                     ReviewService reviewService) {
        this.productService = productService;
        this.inquiryService = inquiryService;
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

        // Inquiry counts
        long totalInquiries = inquiryService.count();
        long pendingInquiries = inquiryService.lambdaQuery()
                .eq(Inquiry::getStatus, 0)
                .count();
        long quotedInquiries = inquiryService.lambdaQuery()
                .eq(Inquiry::getStatus, 1)
                .count();
        stats.put("totalInquiries", totalInquiries);
        stats.put("pendingInquiries", pendingInquiries);
        stats.put("quotedInquiries", quotedInquiries);

        // Revenue (quoted amount)
        java.util.List<Inquiry> allInquiries = inquiryService.list();
        java.math.BigDecimal totalRevenue = allInquiries.stream()
                .filter(i -> i.getStatus() >= 1)
                .map(Inquiry::getQuotationAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
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

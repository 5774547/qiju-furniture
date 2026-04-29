package com.qiju.furniture.module.review.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.review.entity.Review;
import com.qiju.furniture.module.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台评价管理 Controller
 */
@Tag(name = "后台评价管理")
@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "获取所有评价")
    @GetMapping
    public Result<List<Review>> listAllReviews() {
        return Result.ok(reviewService.list());
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.removeById(id);
        return Result.ok();
    }
}

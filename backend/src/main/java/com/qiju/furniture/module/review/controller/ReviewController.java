package com.qiju.furniture.module.review.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.review.entity.Review;
import com.qiju.furniture.module.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Review Controller
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Get reviews by product ID
     *
     * @param productId Product ID
     * @return List of reviews
     */
    @GetMapping("/product/{productId}")
    public Result<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return Result.ok(reviews);
    }

    /**
     * Create a new review
     *
     * @param review Review data
     * @return Created review
     */
    @PostMapping
    public Result<Review> createReview(@Valid @RequestBody Review review) {
        Review created = reviewService.createReview(review);
        return Result.ok(created);
    }
}

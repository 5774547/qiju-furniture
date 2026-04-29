package com.qiju.furniture.module.review.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.review.entity.Review;

import java.util.List;

/**
 * Review Service Interface
 *
 * @author Qiju Team
 */
public interface ReviewService extends IService<Review> {

    /**
     * Get reviews by product ID
     */
    List<Review> getReviewsByProductId(Long productId);

    /**
     * Create a new review
     */
    Review createReview(Review review);
}

package com.qiju.furniture.module.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.module.review.entity.Review;
import com.qiju.furniture.module.review.mapper.ReviewMapper;
import com.qiju.furniture.module.review.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Review Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Review createReview(Review review) {
        review.setStatus(0); // Default to pending
        this.save(review);
        return review;
    }
}

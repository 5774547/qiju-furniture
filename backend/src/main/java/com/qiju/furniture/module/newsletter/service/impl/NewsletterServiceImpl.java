package com.qiju.furniture.module.newsletter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.module.newsletter.entity.Newsletter;
import com.qiju.furniture.module.newsletter.mapper.NewsletterMapper;
import com.qiju.furniture.module.newsletter.service.NewsletterService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Newsletter Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class NewsletterServiceImpl extends ServiceImpl<NewsletterMapper, Newsletter> implements NewsletterService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public Newsletter subscribe(String email) {
        // Validate email format
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessException(400, "Invalid email format");
        }

        String trimmedEmail = email.trim().toLowerCase();

        // Check if already subscribed
        LambdaQueryWrapper<Newsletter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Newsletter::getEmail, trimmedEmail);
        Newsletter existing = this.getOne(wrapper);

        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new BusinessException(400, "Email already subscribed");
            }
            // Re-subscribe
            existing.setStatus(1);
            this.updateById(existing);
            return existing;
        }

        // Create new subscription
        Newsletter newsletter = Newsletter.builder()
                .email(trimmedEmail)
                .status(1)
                .build();
        this.save(newsletter);
        return newsletter;
    }
}

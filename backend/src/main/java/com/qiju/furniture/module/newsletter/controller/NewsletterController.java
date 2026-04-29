package com.qiju.furniture.module.newsletter.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.newsletter.dto.NewsletterSubscribeDTO;
import com.qiju.furniture.module.newsletter.entity.Newsletter;
import com.qiju.furniture.module.newsletter.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Newsletter Controller
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    /**
     * Subscribe email to newsletter
     *
     * @param subscribeDTO Email to subscribe
     * @return Subscription result
     */
    @PostMapping
    public Result<Newsletter> subscribe(@Valid @RequestBody NewsletterSubscribeDTO subscribeDTO) {
        Newsletter newsletter = newsletterService.subscribe(subscribeDTO.getEmail());
        return Result.ok(newsletter);
    }
}

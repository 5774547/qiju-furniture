package com.qiju.furniture.module.newsletter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.newsletter.entity.Newsletter;

/**
 * Newsletter Service Interface
 *
 * @author Qiju Team
 */
public interface NewsletterService extends IService<Newsletter> {

    /**
     * Subscribe email to newsletter
     */
    Newsletter subscribe(String email);
}

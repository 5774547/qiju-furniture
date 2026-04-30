package com.qiju.furniture.module.inquiry.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InquiryListVO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private BigDecimal wholesalePrice;
    private String unit;
    private String category;
    private Integer quantity;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


package com.qiju.furniture.module.inquiry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@TableName("inquiry_item")
public class InquiryItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inquiryId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private BigDecimal wholesalePrice;
    private String unit;
    private Integer quantity;
    private String remark;
}

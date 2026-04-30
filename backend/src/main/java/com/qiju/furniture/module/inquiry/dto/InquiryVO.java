package com.qiju.furniture.module.inquiry.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InquiryVO {
    private Long id;
    private String inquiryNo;
    private Long userId;
    private String customerName;
    private String customerPhone;
    private String customerCompany;
    private String address;
    private Integer status;
    private BigDecimal quotationAmount;
    private String remark;
    private String adminRemark;
    private LocalDateTime validUntil;
    private LocalDateTime createTime;
    private List<InquiryItemVO> items;

    @Data
    public static class InquiryItemVO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private BigDecimal wholesalePrice;
        private String unit;
        private Integer quantity;
        private String remark;
    }
}

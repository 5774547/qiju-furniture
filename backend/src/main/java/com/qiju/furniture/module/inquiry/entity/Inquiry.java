package com.qiju.furniture.module.inquiry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@TableName("inquiry")
public class Inquiry implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String inquiryNo;
    private Long userId;
    private String customerName;
    private String customerPhone;
    private String customerCompany;
    private String address;
    @Builder.Default
    private Integer status = 0;
    private BigDecimal quotationAmount;
    private String remark;
    private String adminRemark;
    private LocalDateTime validUntil;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

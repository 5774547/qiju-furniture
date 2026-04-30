package com.qiju.furniture.module.inquiry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InquiryListAddDTO {
    @NotNull
    private Long productId;
    private Integer quantity;
    private String remark;
}


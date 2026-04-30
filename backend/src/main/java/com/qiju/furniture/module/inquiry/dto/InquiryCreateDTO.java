package com.qiju.furniture.module.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class InquiryCreateDTO {
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerPhone;
    private String customerCompany;
    private String address;
    private String remark;
    private List<Long> itemIds;
}

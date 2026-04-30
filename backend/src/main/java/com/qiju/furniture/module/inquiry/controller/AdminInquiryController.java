package com.qiju.furniture.module.inquiry.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.inquiry.dto.InquiryVO;
import com.qiju.furniture.module.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "后台询价单管理")
@RestController
@RequestMapping("/api/admin/inquiries")
public class AdminInquiryController {

    private final InquiryService inquiryService;

    public AdminInquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @Operation(summary = "获取所有询价单")
    @GetMapping
    public Result<List<InquiryVO>> listAllInquiries() {
        return Result.ok(inquiryService.getAllInquiries());
    }

    @Operation(summary = "获取询价单详情")
    @GetMapping("/{id}")
    public Result<InquiryVO> getInquiryDetail(@PathVariable Long id) {
        return Result.ok(inquiryService.getInquiryDetail(id));
    }

    @Operation(summary = "回复报价")
    @PutMapping("/{id}/quotation")
    public Result<Void> setQuotation(@PathVariable Long id,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam(required = false) String adminRemark,
                                      @RequestParam(required = false) LocalDateTime validUntil) {
        inquiryService.setQuotation(id, amount, adminRemark, validUntil);
        return Result.ok();
    }

    @Operation(summary = "关闭询价单")
    @PutMapping("/{id}/close")
    public Result<Void> closeInquiry(@PathVariable Long id) {
        inquiryService.closeInquiry(id);
        return Result.ok();
    }
}

package com.qiju.furniture.module.inquiry.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.inquiry.dto.InquiryCreateDTO;
import com.qiju.furniture.module.inquiry.dto.InquiryVO;
import com.qiju.furniture.module.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "询价单")
@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @Operation(summary = "创建询价单")
    @PostMapping
    public Result<InquiryVO> createInquiry(Authentication auth, @Valid @RequestBody InquiryCreateDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return Result.ok(inquiryService.createInquiry(dto, userId));
    }

    @Operation(summary = "获取我的询价单列表")
    @GetMapping("/my")
    public Result<List<InquiryVO>> getMyInquiries(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.ok(inquiryService.getMyInquiries(userId));
    }

    @Operation(summary = "获取询价单详情")
    @GetMapping("/{id}")
    public Result<InquiryVO> getInquiryDetail(@PathVariable Long id) {
        return Result.ok(inquiryService.getInquiryDetail(id));
    }
}

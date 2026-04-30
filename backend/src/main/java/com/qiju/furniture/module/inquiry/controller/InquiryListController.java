package com.qiju.furniture.module.inquiry.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.inquiry.dto.InquiryListAddDTO;
import com.qiju.furniture.module.inquiry.dto.InquiryListUpdateDTO;
import com.qiju.furniture.module.inquiry.dto.InquiryListVO;
import com.qiju.furniture.module.inquiry.service.InquiryListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "询价清单")
@RestController
@RequestMapping("/api/inquiry-lists")
public class InquiryListController {

    private final InquiryListService inquiryListService;

    public InquiryListController(InquiryListService inquiryListService) {
        this.inquiryListService = inquiryListService;
    }

    @Operation(summary = "获取我的询价清单")
    @GetMapping
    public Result<List<InquiryListVO>> getInquiryList(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.ok(inquiryListService.getInquiryList(userId));
    }

    @Operation(summary = "添加商品到询价清单")
    @PostMapping
    public Result<InquiryListVO> addItem(Authentication auth, @Valid @RequestBody InquiryListAddDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return Result.ok(inquiryListService.addItem(userId, dto.getProductId(), dto.getQuantity(), dto.getRemark()));
    }

    @Operation(summary = "修改询价清单项")
    @PutMapping("/{id}")
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody InquiryListUpdateDTO dto) {
        inquiryListService.updateItem(id, dto.getQuantity(), dto.getRemark());
        return Result.ok();
    }

    @Operation(summary = "删除询价清单项")
    @DeleteMapping("/{id}")
    public Result<Void> removeItem(@PathVariable Long id) {
        inquiryListService.removeItem(id);
        return Result.ok();
    }

    @Operation(summary = "清空询价清单")
    @DeleteMapping("/clear")
    public Result<Void> clearList(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        inquiryListService.clearList(userId);
        return Result.ok();
    }
}


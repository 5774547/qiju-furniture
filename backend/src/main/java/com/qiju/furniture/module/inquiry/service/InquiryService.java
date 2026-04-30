package com.qiju.furniture.module.inquiry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.inquiry.dto.InquiryCreateDTO;
import com.qiju.furniture.module.inquiry.dto.InquiryVO;
import com.qiju.furniture.module.inquiry.entity.Inquiry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InquiryService extends IService<Inquiry> {
    InquiryVO createInquiry(InquiryCreateDTO dto, Long userId);
    List<InquiryVO> getMyInquiries(Long userId);
    List<InquiryVO> getAllInquiries();
    InquiryVO getInquiryDetail(Long id);
    void setQuotation(Long id, BigDecimal amount, String adminRemark, LocalDateTime validUntil);
    void closeInquiry(Long id);
}

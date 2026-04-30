package com.qiju.furniture.module.inquiry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.inquiry.entity.InquiryList;
import com.qiju.furniture.module.inquiry.dto.InquiryListVO;
import java.util.List;

public interface InquiryListService extends IService<InquiryList> {
    List<InquiryListVO> getInquiryList(Long userId);
    InquiryListVO addItem(Long userId, Long productId, Integer quantity, String remark);
    void updateItem(Long id, Integer quantity, String remark);
    void removeItem(Long id);
    void clearList(Long userId);
}


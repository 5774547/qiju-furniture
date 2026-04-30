package com.qiju.furniture.module.inquiry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.module.inquiry.dto.InquiryCreateDTO;
import com.qiju.furniture.module.inquiry.dto.InquiryVO;
import com.qiju.furniture.module.inquiry.entity.Inquiry;
import com.qiju.furniture.module.inquiry.entity.InquiryItem;
import com.qiju.furniture.module.inquiry.entity.InquiryList;
import com.qiju.furniture.module.inquiry.mapper.InquiryItemMapper;
import com.qiju.furniture.module.inquiry.mapper.InquiryMapper;
import com.qiju.furniture.module.inquiry.service.InquiryListService;
import com.qiju.furniture.module.inquiry.service.InquiryService;
import com.qiju.furniture.module.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class InquiryServiceImpl extends ServiceImpl<InquiryMapper, Inquiry> implements InquiryService {

    private static final Logger log = LoggerFactory.getLogger(InquiryServiceImpl.class);
    private static final AtomicLong INQUIRY_NUM_SEQ = new AtomicLong(0);

    private final InquiryItemMapper inquiryItemMapper;
    private final InquiryListService inquiryListService;
    private final ProductService productService;

    public InquiryServiceImpl(InquiryItemMapper inquiryItemMapper,
                              InquiryListService inquiryListService,
                              ProductService productService) {
        this.inquiryItemMapper = inquiryItemMapper;
        this.inquiryListService = inquiryListService;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InquiryVO createInquiry(InquiryCreateDTO dto, Long userId) {
        String inquiryNo = generateInquiryNo();

        Inquiry inquiry = Inquiry.builder()
                .inquiryNo(inquiryNo)
                .userId(userId)
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .customerCompany(dto.getCustomerCompany())
                .address(dto.getAddress())
                .status(0)
                .remark(dto.getRemark())
                .build();
        this.save(inquiry);

        // Copy items from inquiry list
        if (dto.getItemIds() != null && !dto.getItemIds().isEmpty()) {
            for (Long itemId : dto.getItemIds()) {
                InquiryList listItem = inquiryListService.getById(itemId);
                if (listItem != null) {
                    var product = productService.getById(listItem.getProductId());
                    InquiryItem item = InquiryItem.builder()
                            .inquiryId(inquiry.getId())
                            .productId(listItem.getProductId())
                            .productName(product != null ? product.getName() : "")
                            .productImage(product != null ? product.getImage() : "")
                            .price(product != null ? product.getPrice() : BigDecimal.ZERO)
                            .wholesalePrice(product != null ? product.getWholesalePrice() : BigDecimal.ZERO)
                            .unit(product != null ? product.getUnit() : "件")
                            .quantity(listItem.getQuantity())
                            .remark(listItem.getRemark())
                            .build();
                    inquiryItemMapper.insert(item);
                }
            }
            // Clear inquiry list
            inquiryListService.clearList(userId);
        }

        log.info("Inquiry created: {}", inquiryNo);
        return getInquiryDetail(inquiry.getId());
    }

    @Override
    public List<InquiryVO> getMyInquiries(Long userId) {
        LambdaQueryWrapper<Inquiry> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inquiry::getUserId, userId).orderByDesc(Inquiry::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<InquiryVO> getAllInquiries() {
        LambdaQueryWrapper<Inquiry> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Inquiry::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public InquiryVO getInquiryDetail(Long id) {
        Inquiry inquiry = this.getById(id);
        if (inquiry == null) throw new BusinessException(404, "询价单不存在");
        return convertToVO(inquiry);
    }

    @Override
    public void setQuotation(Long id, BigDecimal amount, String adminRemark, LocalDateTime validUntil) {
        Inquiry inquiry = this.getById(id);
        if (inquiry == null) throw new BusinessException(404, "询价单不存在");
        inquiry.setQuotationAmount(amount);
        inquiry.setAdminRemark(adminRemark);
        inquiry.setValidUntil(validUntil);
        inquiry.setStatus(1);
        this.updateById(inquiry);
        log.info("Quotation set for inquiry {}: {}", inquiry.getInquiryNo(), amount);
    }

    @Override
    public void closeInquiry(Long id) {
        Inquiry inquiry = this.getById(id);
        if (inquiry == null) throw new BusinessException(404, "询价单不存在");
        inquiry.setStatus(3);
        this.updateById(inquiry);
    }

    private InquiryVO convertToVO(Inquiry inquiry) {
        InquiryVO vo = new InquiryVO();
        BeanUtils.copyProperties(inquiry, vo);

        LambdaQueryWrapper<InquiryItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(InquiryItem::getInquiryId, inquiry.getId());
        List<InquiryItem> items = inquiryItemMapper.selectList(itemWrapper);

        List<InquiryVO.InquiryItemVO> itemVOs = items.stream().map(item -> {
            InquiryVO.InquiryItemVO itemVO = new InquiryVO.InquiryItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());

        vo.setItems(itemVOs);
        return vo;
    }

    private synchronized String generateInquiryNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long seq = INQUIRY_NUM_SEQ.incrementAndGet() % 10000;
        return "INQ" + datePart + String.format("%04d", seq);
    }
}

package com.qiju.furniture.module.inquiry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.module.inquiry.entity.InquiryList;
import com.qiju.furniture.module.inquiry.mapper.InquiryListMapper;
import com.qiju.furniture.module.inquiry.service.InquiryListService;
import com.qiju.furniture.module.inquiry.dto.InquiryListVO;
import com.qiju.furniture.module.product.entity.Product;
import com.qiju.furniture.module.product.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryListServiceImpl extends ServiceImpl<InquiryListMapper, InquiryList> implements InquiryListService {

    private final ProductService productService;

    public InquiryListServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public List<InquiryListVO> getInquiryList(Long userId) {
        LambdaQueryWrapper<InquiryList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InquiryList::getUserId, userId);
        wrapper.orderByDesc(InquiryList::getCreateTime);
        List<InquiryList> items = this.list(wrapper);
        return items.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public InquiryListVO addItem(Long userId, Long productId, Integer quantity, String remark) {
        // Check if already exists
        LambdaQueryWrapper<InquiryList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InquiryList::getUserId, userId).eq(InquiryList::getProductId, productId);
        InquiryList existing = this.getOne(wrapper);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + (quantity != null ? quantity : 1));
            this.updateById(existing);
            return convertToVO(existing);
        }
        InquiryList item = InquiryList.builder()
                .userId(userId).productId(productId)
                .quantity(quantity != null ? quantity : 1)
                .remark(remark).build();
        this.save(item);
        return convertToVO(item);
    }

    @Override
    public void updateItem(Long id, Integer quantity, String remark) {
        InquiryList item = this.getById(id);
        if (item != null) {
            if (quantity != null) item.setQuantity(quantity);
            if (remark != null) item.setRemark(remark);
            this.updateById(item);
        }
    }

    @Override
    public void removeItem(Long id) {
        this.removeById(id);
    }

    @Override
    public void clearList(Long userId) {
        LambdaQueryWrapper<InquiryList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InquiryList::getUserId, userId);
        this.remove(wrapper);
    }

    private InquiryListVO convertToVO(InquiryList item) {
        InquiryListVO vo = new InquiryListVO();
        BeanUtils.copyProperties(item, vo);
        Product product = productService.getById(item.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductImage(product.getImage());
            vo.setPrice(product.getPrice());
            vo.setWholesalePrice(product.getWholesalePrice());
            vo.setUnit(product.getUnit());
            vo.setCategory(product.getCategory());
        }
        return vo;
    }
}


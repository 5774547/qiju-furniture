package com.qiju.furniture.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.module.order.dto.OrderCreateDTO;
import com.qiju.furniture.module.order.dto.OrderVO;
import com.qiju.furniture.module.order.entity.Order;
import com.qiju.furniture.module.order.entity.OrderItem;
import com.qiju.furniture.module.order.mapper.OrderItemMapper;
import com.qiju.furniture.module.order.mapper.OrderMapper;
import com.qiju.furniture.module.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Order Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final AtomicLong ORDER_NUMBER_SEQUENCE = new AtomicLong(0);

    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO createDTO) {
        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateDTO.OrderItemDTO item : createDTO.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        // Validate coupon (simple validation - just stores the code)
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (createDTO.getCouponCode() != null && !createDTO.getCouponCode().isEmpty()) {
            // In a real system, this would validate against a coupon table
            // For now, apply a 10% discount as demonstration
            discountAmount = totalAmount.multiply(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);
            log.info("Coupon applied: {}, discount: {}", createDTO.getCouponCode(), discountAmount);
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Generate order number
        String orderNo = generateOrderNo();

        // Create order
        Order order = Order.builder()
                .orderNo(orderNo)
                .sessionId(createDTO.getSessionId())
                .customerName(createDTO.getCustomerName())
                .customerEmail(createDTO.getCustomerEmail())
                .customerPhone(createDTO.getCustomerPhone())
                .address(createDTO.getAddress())
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .couponCode(createDTO.getCouponCode())
                .status(0) // Pending
                .notes(createDTO.getNotes())
                .build();
        this.save(order);

        // Create order items
        for (OrderCreateDTO.OrderItemDTO item : createDTO.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productImage(item.getProductImage())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();
            orderItemMapper.insert(orderItem);
        }

        log.info("Order created successfully: orderNo={}, total={}, discount={}, final={}",
                orderNo, totalAmount, discountAmount, finalAmount);

        return getOrderDetail(order.getId());
    }

    @Override
    public List<OrderVO> getOrdersBySessionId(String sessionId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSessionId, sessionId)
                .orderByDesc(Order::getCreateTime);
        List<Order> orders = this.list(wrapper);
        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        List<Order> orders = this.list(wrapper);
        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        return convertToVO(order);
    }

    /**
     * Convert Order entity to OrderVO with items
     */
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // Get order items
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        List<OrderVO.OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());

        vo.setItems(itemVOs);
        return vo;
    }

    /**
     * Generate unique order number: yyyyMMddHHmmss + sequence
     */
    private synchronized String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long seq = ORDER_NUMBER_SEQUENCE.incrementAndGet() % 10000;
        return "ORD" + datePart + String.format("%04d", seq);
    }
}

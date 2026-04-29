package com.qiju.furniture.module.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.order.dto.OrderCreateDTO;
import com.qiju.furniture.module.order.dto.OrderVO;
import com.qiju.furniture.module.order.entity.Order;

import java.util.List;

/**
 * Order Service Interface
 *
 * @author Qiju Team
 */
public interface OrderService extends IService<Order> {

    /**
     * Create order from cart items
     */
    OrderVO createOrder(OrderCreateDTO createDTO);

    /**
     * Get orders by session ID
     */
    List<OrderVO> getOrdersBySessionId(String sessionId);

    /**
     * Get orders by user ID
     */
    List<OrderVO> getOrdersByUserId(Long userId);

    /**
     * Get order detail by ID
     */
    OrderVO getOrderDetail(Long id);
}

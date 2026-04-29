package com.qiju.furniture.module.order.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.order.dto.OrderVO;
import com.qiju.furniture.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台订单管理 Controller
 */
@Tag(name = "后台订单管理")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "获取所有订单")
    @GetMapping
    public Result<List<OrderVO>> listAllOrders() {
        java.util.List<com.qiju.furniture.module.order.entity.Order> orders = orderService.list();
        return Result.ok(orders.stream()
                .map(order -> orderService.getOrderDetail(order.getId()))
                .toList());
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        com.qiju.furniture.module.order.entity.Order order = orderService.getById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        order.setStatus(status);
        orderService.updateById(order);
        return Result.ok();
    }
}

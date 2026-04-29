package com.qiju.furniture.module.order.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.order.dto.OrderCreateDTO;
import com.qiju.furniture.module.order.dto.OrderVO;
import com.qiju.furniture.module.order.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Order Controller
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        OrderVO order = orderService.createOrder(createDTO);
        return Result.ok(order);
    }

    @GetMapping
    public Result<List<OrderVO>> listOrders(@RequestParam String sessionId) {
        return Result.ok(orderService.getOrdersBySessionId(sessionId));
    }

    @GetMapping("/my")
    public Result<List<OrderVO>> listMyOrders() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long userId) {
            return Result.ok(orderService.getOrdersByUserId(userId));
        }
        return Result.error(401, "User not authenticated");
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderVO> orders;
        if (principal instanceof Long userId) {
            orders = orderService.getOrdersByUserId(userId);
        } else {
            orders = List.of();
        }

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("订单列表");

        // Header
        Row header = sheet.createRow(0);
        String[] cols = {"订单号", "总金额", "实付", "状态", "联系人", "电话", "地址", "创建时间"};
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowIdx = 1;
        for (OrderVO o : orders) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(o.getOrderNo() != null ? o.getOrderNo() : "");
            row.createCell(1).setCellValue(o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0);
            row.createCell(2).setCellValue(o.getFinalAmount() != null ? o.getFinalAmount().doubleValue() : 0);
            row.createCell(3).setCellValue(getStatusText(o.getStatus()));
            row.createCell(4).setCellValue(o.getCustomerName() != null ? o.getCustomerName() : "");
            row.createCell(5).setCellValue(o.getCustomerPhone() != null ? o.getCustomerPhone() : "");
            row.createCell(6).setCellValue(o.getAddress() != null ? o.getAddress() : "");
            row.createCell(7).setCellValue(o.getCreateTime() != null ? o.getCreateTime().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        String filename = "orders-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"");
        wb.write(response.getOutputStream());
        wb.close();
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}

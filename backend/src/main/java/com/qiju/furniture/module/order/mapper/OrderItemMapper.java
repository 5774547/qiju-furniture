package com.qiju.furniture.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiju.furniture.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * Order Item Mapper
 *
 * @author Qiju Team
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}

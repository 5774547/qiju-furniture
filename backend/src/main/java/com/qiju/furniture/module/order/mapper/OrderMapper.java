package com.qiju.furniture.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiju.furniture.module.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * Order Mapper
 *
 * @author Qiju Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

package com.qiju.furniture.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiju.furniture.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper
 *
 * @author Qiju Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
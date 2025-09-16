package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper接口
 *
 * @author campus-secondhand
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
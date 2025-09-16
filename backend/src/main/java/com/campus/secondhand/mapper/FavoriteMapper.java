package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 收藏数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 分页查询用户收藏的商品
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 商品分页数据
     */
    @Select("SELECT p.* FROM products p INNER JOIN favorites f ON p.id = f.product_id " +
            "WHERE f.user_id = #{userId} AND p.deleted = 0 " +
            "ORDER BY f.created_at DESC")
    Page<Product> selectUserFavoriteProducts(Page<Product> page, @Param("userId") Long userId);
}
package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 分页查询分类列表
     */
    @Select("<script>" +
            "SELECT * FROM category WHERE deleted = 0" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY sort_order ASC, created_time DESC" +
            "</script>")
    IPage<Category> selectCategoryPage(Page<Category> page, @Param("keyword") String keyword, @Param("status") Integer status);
    
    /**
     * 查询所有启用的分类
     */
    @Select("SELECT * FROM category WHERE deleted = 0 AND status = 1 ORDER BY sort_order ASC, created_time DESC")
    List<Category> selectEnabledCategories();
    
    /**
     * 根据名称查询分类
     */
    @Select("SELECT * FROM category WHERE name = #{name} AND deleted = 0")
    Category findByName(@Param("name") String name);
    
    /**
     * 检查分类下是否有商品
     */
    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId} AND deleted = 0")
    Long countProductsByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 统计分类数量
     */
    @Select("SELECT COUNT(*) FROM category WHERE deleted = 0")
    Long countCategories();
}
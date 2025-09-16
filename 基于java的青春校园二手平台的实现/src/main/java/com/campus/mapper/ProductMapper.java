package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 分页查询商品列表（用户端）
     */
    @Select("<script>" +
            "SELECT p.*, c.name as categoryName, u.nickname as sellerName FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.deleted = 0 AND p.status = 1" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            " AND p.category_id = #{categoryId}" +
            "</if>" +
            "<if test='minPrice != null'>" +
            " AND p.price >= #{minPrice}" +
            "</if>" +
            "<if test='maxPrice != null'>" +
            " AND p.price <= #{maxPrice}" +
            "</if>" +
            " ORDER BY p.created_time DESC" +
            "</script>")
    IPage<Product> selectProductPage(Page<Product> page, @Param("keyword") String keyword,
                                   @Param("categoryId") Long categoryId, @Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice);
    
    /**
     * 分页查询商品列表（管理端）
     */
    @Select("<script>" +
            "SELECT p.*, c.name as categoryName, u.nickname as sellerName FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "LEFT JOIN user u ON p.seller_id = u.id " +
            "WHERE p.deleted = 0" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            " AND p.category_id = #{categoryId}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND p.status = #{status}" +
            "</if>" +
            "<if test='sellerId != null'>" +
            " AND p.seller_id = #{sellerId}" +
            "</if>" +
            " ORDER BY p.created_time DESC" +
            "</script>")
    IPage<Product> selectProductPageForAdmin(Page<Product> page, @Param("keyword") String keyword,
                                           @Param("categoryId") Long categoryId, @Param("status") Integer status,
                                           @Param("sellerId") Long sellerId);
    
    /**
     * 查询用户发布的商品
     */
    @Select("<script>" +
            "SELECT p.*, c.name as categoryName FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.deleted = 0 AND p.seller_id = #{sellerId}" +
            "<if test='status != null'>" +
            " AND p.status = #{status}" +
            "</if>" +
            " ORDER BY p.created_time DESC" +
            "</script>")
    IPage<Product> selectUserProducts(Page<Product> page, @Param("sellerId") Long sellerId, @Param("status") Integer status);
    
    /**
     * 增加浏览次数
     */
    @Update("UPDATE product SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 增加点赞次数
     */
    @Update("UPDATE product SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);
    
    /**
     * 统计商品数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0")
    Long countProducts();
    
    /**
     * 统计今日新增商品数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE DATE(created_time) = CURDATE() AND deleted = 0")
    Long countTodayProducts();
    
    /**
     * 统计各状态商品数量
     */
    @Select("SELECT status, COUNT(*) as count FROM product WHERE deleted = 0 GROUP BY status")
    List<Object> countProductsByStatus();
    
    /**
     * 统计各分类商品数量
     */
    @Select("SELECT c.name, COUNT(p.id) as count FROM category c " +
            "LEFT JOIN product p ON c.id = p.category_id AND p.deleted = 0 AND p.status = 1 " +
            "WHERE c.deleted = 0 GROUP BY c.id, c.name ORDER BY count DESC")
    List<Object> countProductsByCategory();
}
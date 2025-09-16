package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 分页查询订单列表（管理端）
     */
    @Select("<script>" +
            "SELECT o.*, bu.nickname as buyerName, su.nickname as sellerName FROM `order` o " +
            "LEFT JOIN user bu ON o.buyer_id = bu.id " +
            "LEFT JOIN user su ON o.seller_id = su.id " +
            "WHERE o.deleted = 0" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (o.order_no LIKE CONCAT('%', #{keyword}, '%') OR o.product_title LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            " AND o.status = #{status}" +
            "</if>" +
            "<if test='buyerId != null'>" +
            " AND o.buyer_id = #{buyerId}" +
            "</if>" +
            "<if test='sellerId != null'>" +
            " AND o.seller_id = #{sellerId}" +
            "</if>" +
            " ORDER BY o.created_time DESC" +
            "</script>")
    IPage<Order> selectOrderPage(Page<Order> page, @Param("keyword") String keyword,
                               @Param("status") Integer status, @Param("buyerId") Long buyerId,
                               @Param("sellerId") Long sellerId);
    
    /**
     * 查询用户购买的订单
     */
    @Select("<script>" +
            "SELECT o.*, su.nickname as sellerName FROM `order` o " +
            "LEFT JOIN user su ON o.seller_id = su.id " +
            "WHERE o.deleted = 0 AND o.buyer_id = #{buyerId}" +
            "<if test='status != null'>" +
            " AND o.status = #{status}" +
            "</if>" +
            " ORDER BY o.created_time DESC" +
            "</script>")
    IPage<Order> selectBuyerOrders(Page<Order> page, @Param("buyerId") Long buyerId, @Param("status") Integer status);
    
    /**
     * 查询用户销售的订单
     */
    @Select("<script>" +
            "SELECT o.*, bu.nickname as buyerName FROM `order` o " +
            "LEFT JOIN user bu ON o.buyer_id = bu.id " +
            "WHERE o.deleted = 0 AND o.seller_id = #{sellerId}" +
            "<if test='status != null'>" +
            " AND o.status = #{status}" +
            "</if>" +
            " ORDER BY o.created_time DESC" +
            "</script>")
    IPage<Order> selectSellerOrders(Page<Order> page, @Param("sellerId") Long sellerId, @Param("status") Integer status);
    
    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo} AND deleted = 0")
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 统计订单数量
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE deleted = 0")
    Long countOrders();
    
    /**
     * 统计今日新增订单数量
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE DATE(created_time) = CURDATE() AND deleted = 0")
    Long countTodayOrders();
    
    /**
     * 统计各状态订单数量
     */
    @Select("SELECT status, COUNT(*) as count FROM `order` WHERE deleted = 0 GROUP BY status")
    List<Object> countOrdersByStatus();
    
    /**
     * 统计交易金额
     */
    @Select("SELECT SUM(price) FROM `order` WHERE deleted = 0 AND status = 3")
    Double sumTotalAmount();
    
    /**
     * 统计今日交易金额
     */
    @Select("SELECT SUM(price) FROM `order` WHERE DATE(created_time) = CURDATE() AND deleted = 0 AND status = 3")
    Double sumTodayAmount();
    
    /**
     * 统计最近7天每日订单数量
     */
    @Select("SELECT DATE(created_time) as date, COUNT(*) as count " +
            "FROM `order` WHERE created_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND deleted = 0 " +
            "GROUP BY DATE(created_time) ORDER BY date")
    List<Object> countOrdersLast7Days();
    
    /**
     * 统计最近7天每日交易金额
     */
    @Select("SELECT DATE(created_time) as date, SUM(price) as amount " +
            "FROM `order` WHERE created_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND deleted = 0 AND status = 3 " +
            "GROUP BY DATE(created_time) ORDER BY date")
    List<Object> sumAmountLast7Days();
}
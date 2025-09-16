package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.PageResult;
import com.campus.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 * 
 * @author Campus Team
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 发布商品
     */
    void publishProduct(Product product);
    
    /**
     * 分页查询商品列表（用户端）
     */
    PageResult<Product> getProductPage(Long current, Long size, String keyword, Long categoryId, 
                                     Double minPrice, Double maxPrice);
    
    /**
     * 分页查询商品列表（管理端）
     */
    PageResult<Product> getProductPageForAdmin(Long current, Long size, String keyword, 
                                             Long categoryId, Integer status, Long sellerId);
    
    /**
     * 查询用户发布的商品
     */
    PageResult<Product> getUserProducts(Long current, Long size, Long sellerId, Integer status);
    
    /**
     * 获取商品详情
     */
    Product getProductDetail(Long productId);
    
    /**
     * 更新商品信息
     */
    void updateProduct(Long productId, Product product);
    
    /**
     * 删除商品
     */
    void deleteProduct(Long productId);
    
    /**
     * 商品上架
     */
    void onlineProduct(Long productId);
    
    /**
     * 商品下架
     */
    void offlineProduct(Long productId);
    
    /**
     * 审核商品
     */
    void auditProduct(Long productId, Integer status, String remark, Long auditUserId);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long productId);
    
    /**
     * 增加点赞次数
     */
    void incrementLikeCount(Long productId);
    
    /**
     * 标记商品为已售出
     */
    void markAsSoldOut(Long productId);
    
    /**
     * 获取热门商品
     */
    List<Product> getHotProducts(Integer limit);
    
    /**
     * 获取最新商品
     */
    List<Product> getLatestProducts(Integer limit);
    
    /**
     * 获取推荐商品
     */
    List<Product> getRecommendProducts(Long userId, Integer limit);
    
    /**
     * 搜索商品
     */
    PageResult<Product> searchProducts(Long current, Long size, String keyword);
    
    /**
     * 获取商品统计信息
     */
    Map<String, Object> getProductStatistics();
    
    /**
     * 检查用户是否可以编辑商品
     */
    boolean canEditProduct(Long userId, Long productId);
    
    /**
     * 检查商品是否可以购买
     */
    boolean canBuyProduct(Long userId, Long productId);
}
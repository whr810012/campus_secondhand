package com.campus.secondhand.service;

import com.campus.secondhand.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 商品服务接口
 *
 * @author campus-secondhand
 */
public interface ProductService {

    /**
     * 分页查询商品列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词
     * @param sortBy 排序方式
     * @return 商品分页数据
     */
    Page<Product> getProductPage(int page, int size, String keyword, String sortBy);

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Product getProductById(Long id);

    /**
     * 获取当前用户的商品列表
     *
     * @param token 用户token
     * @param page 页码
     * @param size 每页大小
     * @param status 商品状态过滤
     * @return 用户商品分页数据
     */
    Page<Product> getMyProducts(String token, int page, int size, String status);

    /**
     * 创建商品
     *
     * @param product 商品信息
     * @param token 用户token
     * @return 创建的商品
     */
    Product createProduct(Product product, String token);

    /**
     * 更新商品信息
     *
     * @param productId 商品ID
     * @param product 商品信息
     * @param token 用户token
     * @return 更新后的商品
     */
    Product updateProduct(Long productId, Product product, String token);

    /**
     * 删除商品
     *
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteProduct(Long productId, Long userId);

    /**
     * 获取相关商品推荐
     *
     * @param productId 商品ID
     * @param size 推荐数量
     * @return 相关商品分页数据
     */
    Page<Product> getRelatedProducts(Long productId, int size);

    /**
     * 根据用户ID获取用户的商品列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 商品状态过滤
     * @return 用户商品分页数据
     */
    Page<Product> getUserProducts(Long userId, int page, int size, String status);

    /**
     * 更新商品状态
     *
     * @param productId 商品ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updateProductStatus(Long productId, String status);
}
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
}
package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询商品列表
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认12
     * @param keyword 搜索关键词
     * @param sortBy 排序方式：latest(最新), price_asc(价格升序), price_desc(价格降序)
     * @return 商品分页数据
     */
    @GetMapping
    public Result<Page<Product>> getProductPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "latest") String sortBy) {
        
        Page<Product> productPage = productService.getProductPage(page, size, keyword, sortBy);
        return Result.success(productPage);
    }

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    /**
     * 获取当前用户的商品列表
     *
     * @param token 用户token
     * @param page 页码，默认1
     * @param size 每页大小，默认12
     * @param status 商品状态过滤
     * @return 用户商品分页数据
     */
    @GetMapping("/my")
    public Result<Page<Product>> getMyProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String status) {
        try {
            Page<Product> productPage = productService.getMyProducts(token, page, size, status);
            return Result.success(productPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建商品
     *
     * @param product 商品信息
     * @param token 用户token
     * @return 创建结果
     */
    @PostMapping
    public Result<Product> createProduct(
            @RequestBody Product product,
            @RequestHeader("Authorization") String token) {
        try {
            Product createdProduct = productService.createProduct(product, token);
            return Result.success(createdProduct);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
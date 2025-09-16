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
}
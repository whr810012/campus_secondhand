package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商品服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Page<Product> getProductPage(int page, int size, String keyword, String sortBy) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的商品
        queryWrapper.eq(Product::getDeleted, 0);
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Product::getTitle, keyword)
                .or()
                .like(Product::getDescription, keyword)
            );
        }
        
        // 排序
        if ("latest".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getCreatedAt);
        } else if ("price_asc".equals(sortBy)) {
            queryWrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getPrice);
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc(Product::getCreatedAt);
        }
        
        return productMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
}
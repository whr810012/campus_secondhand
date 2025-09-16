package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类Mapper接口
 *
 * @author campus-secondhand
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取启用状态的分类列表
     *
     * @return 启用的分类列表
     */
    @Select("SELECT * FROM categories WHERE status = 1 AND deleted = 0 ORDER BY sort ASC, id ASC")
    List<Category> selectEnabledCategories();

    /**
     * 根据父分类ID获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort ASC, id ASC")
    List<Category> selectByParentId(Long parentId);

}
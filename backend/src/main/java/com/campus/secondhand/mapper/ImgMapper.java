package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.Img;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图片Mapper接口
 *
 * @author campus-secondhand
 */
@Mapper
public interface ImgMapper extends BaseMapper<Img> {

    /**
     * 根据用户ID查询图片列表
     *
     * @param userId 用户ID
     * @return 图片列表
     */
    @Select("SELECT * FROM imgs WHERE user_id = #{userId} AND status = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<Img> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据状态查询图片列表
     *
     * @param status 状态
     * @return 图片列表
     */
    @Select("SELECT * FROM imgs WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<Img> selectByStatus(@Param("status") Integer status);
}
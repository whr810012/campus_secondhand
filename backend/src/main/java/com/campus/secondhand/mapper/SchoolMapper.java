package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.School;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学校Mapper接口
 *
 * @author campus-secondhand
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {

    /**
     * 获取启用状态的学校列表
     *
     * @return 启用的学校列表
     */
    @Select("SELECT * FROM schools WHERE status = 1 AND deleted = 0 ORDER BY province ASC, city ASC, name ASC")
    List<School> selectEnabledSchools();

    /**
     * 根据省份获取学校列表
     *
     * @param province 省份
     * @return 学校列表
     */
    @Select("SELECT * FROM schools WHERE province = #{province} AND status = 1 AND deleted = 0 ORDER BY city ASC, name ASC")
    List<School> selectSchoolsByProvince(String province);

    /**
     * 根据城市获取学校列表
     *
     * @param city 城市
     * @return 学校列表
     */
    @Select("SELECT * FROM schools WHERE city = #{city} AND status = 1 AND deleted = 0 ORDER BY name ASC")
    List<School> selectSchoolsByCity(String city);

}
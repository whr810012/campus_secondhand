package com.campus.secondhand.service;

import com.campus.secondhand.entity.School;

import java.util.List;

/**
 * 学校服务接口
 *
 * @author campus-secondhand
 */
public interface SchoolService {

    /**
     * 获取所有学校列表
     *
     * @return 学校列表
     */
    List<School> getAllSchools();

    /**
     * 获取启用状态的学校列表
     *
     * @return 启用的学校列表
     */
    List<School> getEnabledSchools();

    /**
     * 根据ID获取学校
     *
     * @param id 学校ID
     * @return 学校信息
     */
    School getSchoolById(Long id);

    /**
     * 根据省份获取学校列表
     *
     * @param province 省份
     * @return 学校列表
     */
    List<School> getSchoolsByProvince(String province);

    /**
     * 根据城市获取学校列表
     *
     * @param city 城市
     * @return 学校列表
     */
    List<School> getSchoolsByCity(String city);

    /**
     * 根据名称搜索学校
     *
     * @param name 学校名称关键词
     * @return 学校列表
     */
    List<School> searchSchoolsByName(String name);

}
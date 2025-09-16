package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.entity.School;
import com.campus.secondhand.mapper.SchoolMapper;
import com.campus.secondhand.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 学校服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolMapper schoolMapper;

    @Override
    public List<School> getAllSchools() {
        LambdaQueryWrapper<School> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(School::getDeleted, 0)
                   .orderByAsc(School::getProvince, School::getCity, School::getName);
        return schoolMapper.selectList(queryWrapper);
    }

    @Override
    public List<School> getEnabledSchools() {
        return schoolMapper.selectEnabledSchools();
    }

    @Override
    public School getSchoolById(Long id) {
        if (id == null) {
            return null;
        }
        return schoolMapper.selectById(id);
    }

    @Override
    public List<School> getSchoolsByProvince(String province) {
        if (!StringUtils.hasText(province)) {
            return getEnabledSchools();
        }
        return schoolMapper.selectSchoolsByProvince(province);
    }

    @Override
    public List<School> getSchoolsByCity(String city) {
        if (!StringUtils.hasText(city)) {
            return getEnabledSchools();
        }
        return schoolMapper.selectSchoolsByCity(city);
    }

    @Override
    public List<School> searchSchoolsByName(String name) {
        if (!StringUtils.hasText(name)) {
            return getEnabledSchools();
        }
        
        LambdaQueryWrapper<School> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(School::getName, name)
                   .eq(School::getStatus, 1)
                   .eq(School::getDeleted, 0)
                   .orderByAsc(School::getProvince, School::getCity, School::getName);
        return schoolMapper.selectList(queryWrapper);
    }

}
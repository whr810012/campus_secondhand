package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.School;
import com.campus.secondhand.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * 获取所有学校列表
     */
    @GetMapping
    public Result<List<School>> getAllSchools() {
        List<School> schools = schoolService.getEnabledSchools();
        return Result.success(schools);
    }

    /**
     * 根据ID获取学校详情
     */
    @GetMapping("/{id}")
    public Result<School> getSchoolById(@PathVariable Long id) {
        School school = schoolService.getSchoolById(id);
        if (school == null) {
            return Result.error("学校不存在");
        }
        return Result.success(school);
    }

    /**
     * 根据省份获取学校列表
     */
    @GetMapping("/province/{province}")
    public Result<List<School>> getSchoolsByProvince(@PathVariable String province) {
        List<School> schools = schoolService.getSchoolsByProvince(province);
        return Result.success(schools);
    }

    /**
     * 根据城市获取学校列表
     */
    @GetMapping("/city/{city}")
    public Result<List<School>> getSchoolsByCity(@PathVariable String city) {
        List<School> schools = schoolService.getSchoolsByCity(city);
        return Result.success(schools);
    }

    /**
     * 搜索学校
     */
    @GetMapping("/search")
    public Result<List<School>> searchSchools(@RequestParam(required = false) String name) {
        List<School> schools = schoolService.searchSchoolsByName(name);
        return Result.success(schools);
    }

}
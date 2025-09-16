package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户地址Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
    
    /**
     * 查询用户的所有地址
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND deleted = 0 ORDER BY is_default DESC, created_time DESC")
    List<UserAddress> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的默认地址
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND is_default = 1 AND deleted = 0")
    UserAddress selectDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 取消用户的所有默认地址
     */
    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{userId} AND deleted = 0")
    void cancelDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户地址数量
     */
    @Select("SELECT COUNT(*) FROM user_address WHERE user_id = #{userId} AND deleted = 0")
    Long countByUserId(@Param("userId") Long userId);
}
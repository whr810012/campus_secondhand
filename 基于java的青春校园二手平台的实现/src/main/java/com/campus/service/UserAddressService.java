package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.UserAddress;

import java.util.List;

/**
 * 用户地址服务接口
 * 
 * @author Campus Team
 */
public interface UserAddressService extends IService<UserAddress> {
    
    /**
     * 添加用户地址
     */
    void addAddress(UserAddress address);
    
    /**
     * 查询用户的所有地址
     */
    List<UserAddress> getUserAddresses(Long userId);
    
    /**
     * 查询用户的默认地址
     */
    UserAddress getDefaultAddress(Long userId);
    
    /**
     * 更新地址信息
     */
    void updateAddress(Long addressId, UserAddress address);
    
    /**
     * 删除地址
     */
    void deleteAddress(Long addressId);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
    
    /**
     * 检查用户是否可以操作地址
     */
    boolean canOperateAddress(Long userId, Long addressId);
    
    /**
     * 统计用户地址数量
     */
    Long countUserAddresses(Long userId);
}
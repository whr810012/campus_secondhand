package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.UserAddress;
import com.campus.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户地址控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/user-address")
@CrossOrigin
public class UserAddressController {
    
    @Autowired
    private UserAddressService userAddressService;
    
    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result<Void> addAddress(@RequestBody UserAddress userAddress) {
        try {
            userAddressService.addAddress(userAddress);
            return Result.success("地址添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询用户所有地址
     */
    @GetMapping("/user/{userId}")
    public Result<List<UserAddress>> getUserAddresses(@PathVariable Long userId) {
        try {
            List<UserAddress> addresses = userAddressService.getUserAddresses(userId);
            return Result.success(addresses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询用户默认地址
     */
    @GetMapping("/default/{userId}")
    public Result<UserAddress> getDefaultAddress(@PathVariable Long userId) {
        try {
            UserAddress address = userAddressService.getDefaultAddress(userId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    public Result<UserAddress> getAddressDetail(@PathVariable Long addressId) {
        try {
            UserAddress address = userAddressService.getById(addressId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    public Result<Void> updateAddress(@PathVariable Long addressId, @RequestBody UserAddress userAddress) {
        try {
            userAddressService.updateAddress(addressId, userAddress);
            return Result.success("地址更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        try {
            userAddressService.deleteAddress(addressId);
            return Result.success("地址删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/default/{addressId}")
    public Result<Void> setDefaultAddress(@PathVariable Long addressId) {
        try {
            userAddressService.setDefaultAddress(addressId);
            return Result.success("默认地址设置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户地址数量
     */
    @GetMapping("/count/{userId}")
    public Result<Long> getUserAddressCount(@PathVariable Long userId) {
        try {
            Long count = userAddressService.getUserAddressCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除地址
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteAddresses(@RequestBody Map<String, List<Long>> params) {
        try {
            List<Long> addressIds = params.get("addressIds");
            for (Long addressId : addressIds) {
                userAddressService.deleteAddress(addressId);
            }
            return Result.success("批量删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
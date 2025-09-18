package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Img;

import java.util.List;

/**
 * 图片服务接口
 *
 * @author campus-secondhand
 */
public interface ImgService extends IService<Img> {

    /**
     * 保存图片到数据库
     *
     * @param name 图片名称
     * @param base64Data base64数据
     * @param userId 用户ID
     * @return 图片实体
     */
    Img saveImg(String name, String base64Data, Long userId);

    /**
     * 根据用户ID查询图片列表
     *
     * @param userId 用户ID
     * @return 图片列表
     */
    List<Img> getImgsByUserId(Long userId);

    /**
     * 根据ID获取图片
     *
     * @param id 图片ID
     * @return 图片实体
     */
    Img getImgById(Long id);

    /**
     * 删除图片
     *
     * @param id 图片ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteImg(Long id, Long userId);
}
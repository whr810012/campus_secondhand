package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.mapper.ImgMapper;
import com.campus.secondhand.service.ImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImgServiceImpl extends ServiceImpl<ImgMapper, Img> implements ImgService {

    private final ImgMapper imgMapper;

    @Override
    public Img saveImg(String name, String base64Data, Long userId) {
        Img img = new Img();
        img.setName(name);
        img.setBase64Data(base64Data);
        img.setUserId(userId);
        img.setStatus(1); // 正常状态
        
        boolean saved = save(img);
        if (saved) {
            log.info("图片保存成功，ID: {}, 用户ID: {}", img.getId(), userId);
            return img;
        } else {
            log.error("图片保存失败，用户ID: {}", userId);
            return null;
        }
    }

    @Override
    public List<Img> getImgsByUserId(Long userId) {
        return imgMapper.selectByUserId(userId);
    }

    @Override
    public Img getImgById(Long id) {
        QueryWrapper<Img> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("status", 1)
                   .eq("deleted", 0);
        return getOne(queryWrapper);
    }

    @Override
    public boolean deleteImg(Long id, Long userId) {
        // 先查询图片是否存在且属于该用户
        QueryWrapper<Img> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("upload_user_id", userId)
                   .eq("deleted", 0);
        
        Img img = getOne(queryWrapper);
        if (img == null) {
            log.warn("图片不存在或无权限删除，ID: {}, 用户ID: {}", id, userId);
            return false;
        }
        
        // 逻辑删除
        img.setStatus(0);
        img.setDeleted(1);
        boolean updated = updateById(img);
        
        if (updated) {
            log.info("图片删除成功，ID: {}, 用户ID: {}", id, userId);
        } else {
            log.error("图片删除失败，ID: {}, 用户ID: {}", id, userId);
        }
        
        return updated;
    }
}
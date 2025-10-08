package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.entity.Announcement;
import com.campus.secondhand.mapper.AnnouncementMapper;
import com.campus.secondhand.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告服务实现类
 */
@Service
@Transactional
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> getAnnouncementPage(Page<Announcement> page, String keyword, String type, Integer status) {
        IPage<Announcement> result;
        
        // 根据不同条件查询
        if (StringUtils.hasText(keyword)) {
            result = announcementMapper.selectAnnouncementPageByKeyword(page, keyword);
        } else if (StringUtils.hasText(type)) {
            result = announcementMapper.selectAnnouncementPageByType(page, type);
        } else if (status != null) {
            result = announcementMapper.selectAnnouncementPageByStatus(page, status);
        } else {
            result = announcementMapper.selectAnnouncementPage(page);
        }
        
        // 处理前端需要的额外字段
        result.getRecords().forEach(this::processAnnouncementFields);
        
        return result;
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.selectAnnouncementById(id);
        if (announcement != null) {
            processAnnouncementFields(announcement);
        }
        return announcement;
    }

    @Override
    public boolean createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setViewCount(0);
        announcement.setDeleted(0);
        
        // 如果设置了发布时间且状态为发布，则立即发布
        if (announcement.getPublishTime() != null && announcement.getStatus() == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        
        return announcementMapper.insert(announcement) > 0;
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcementMapper.updateById(announcement) > 0;
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        return announcementMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeleteAnnouncements(List<Long> ids) {
        return announcementMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean publishAnnouncement(Long id) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(1); // 1-已发布
        announcement.setPublishTime(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcementMapper.updateById(announcement) > 0;
    }

    @Override
    public boolean offlineAnnouncement(Long id) {
        return announcementMapper.updateAnnouncementStatus(id, 2) > 0; // 2-已下线
    }

    @Override
    public boolean batchPublishAnnouncements(List<Long> ids) {
        return announcementMapper.batchUpdateStatus(ids, 1) > 0; // 1-已发布
    }

    @Override
    public boolean batchOfflineAnnouncements(List<Long> ids) {
        return announcementMapper.batchUpdateStatus(ids, 2) > 0; // 2-已下线
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return announcementMapper.incrementViewCount(id) > 0;
    }

    @Override
    public Map<String, Object> getAnnouncementStats() {
        List<Object> stats = announcementMapper.getAnnouncementStats();
        Map<String, Object> result = new HashMap<>();
        
        if (!stats.isEmpty()) {
            Map<String, Object> data = (Map<String, Object>) stats.get(0);
            result.put("published", data.get("published"));
            result.put("draft", data.get("draft"));
            result.put("offline", data.get("offline"));
            result.put("total", data.get("total"));
        } else {
            result.put("published", 0);
            result.put("draft", 0);
            result.put("offline", 0);
            result.put("total", 0);
        }
        
        return result;
    }

    /**
     * 处理公告字段，添加前端需要的额外信息
     */
    private void processAnnouncementFields(Announcement announcement) {
        // 设置状态标签
        switch (announcement.getStatus()) {
            case 0:
                announcement.setStatusLabel("草稿");
                break;
            case 1:
                announcement.setStatusLabel("已发布");
                break;
            case 2:
                announcement.setStatusLabel("已下线");
                break;
            default:
                announcement.setStatusLabel("未知");
        }
        
        // 设置类型标签
        switch (announcement.getType()) {
            case "normal":
                announcement.setTypeLabel("普通公告");
                break;
            case "important":
                announcement.setTypeLabel("重要公告");
                break;
            case "urgent":
                announcement.setTypeLabel("紧急公告");
                break;
            case "system":
                announcement.setTypeLabel("系统公告");
                break;
            case "activity":
                announcement.setTypeLabel("活动公告");
                break;
            case "maintenance":
                announcement.setTypeLabel("维护公告");
                break;
            default:
                announcement.setTypeLabel("其他");
        }
        
        // 设置是否置顶（这里可以根据业务逻辑判断，暂时设为false）
        announcement.setIsTop(false);
    }
}
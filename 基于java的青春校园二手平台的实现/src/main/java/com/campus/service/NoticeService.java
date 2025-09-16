package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.PageResult;
import com.campus.entity.Notice;

import java.util.List;
import java.util.Map;

/**
 * 系统公告服务接口
 * 
 * @author Campus Team
 */
public interface NoticeService extends IService<Notice> {
    
    /**
     * 创建公告
     */
    void createNotice(Notice notice);
    
    /**
     * 分页查询公告列表（管理端）
     */
    PageResult<Notice> getNoticePage(Long current, Long size, String keyword, 
                                   Integer type, Integer status);
    
    /**
     * 查询启用的公告列表（用户端）
     */
    List<Notice> getEnabledNotices(Integer type, Integer limit);
    
    /**
     * 获取公告详情
     */
    Notice getNoticeDetail(Long noticeId);
    
    /**
     * 更新公告信息
     */
    void updateNotice(Long noticeId, Notice notice);
    
    /**
     * 删除公告
     */
    void deleteNotice(Long noticeId);
    
    /**
     * 启用/禁用公告
     */
    void updateNoticeStatus(Long noticeId, Integer status);
    
    /**
     * 更新公告排序
     */
    void updateNoticeSort(Long noticeId, Integer sortOrder);
    
    /**
     * 获取公告统计信息
     */
    Map<String, Object> getNoticeStatistics();
    
    /**
     * 获取最新公告
     */
    List<Notice> getLatestNotices(Integer limit);
}
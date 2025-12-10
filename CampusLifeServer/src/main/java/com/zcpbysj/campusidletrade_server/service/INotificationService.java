package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Notification;
import com.zcpbysj.campusidletrade_server.entity.vo.notification.NotificationVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

import java.util.Map;

/**
 * 通知服务接口
 */
public interface INotificationService extends IService<Notification> {
    
    /**
     * 获取通知列表
     */
    Map<String, Object> getNotifications(Long userId, Integer page, Integer size);
    
    /**
     * 标记已读
     */
    void markAsRead(Long id, Long userId);
    
    /**
     * 全部已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 发送通知
     */
    void sendNotification(Long userId, String title, String content, String type, Long relatedId);
}

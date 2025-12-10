package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Notification;
import com.zcpbysj.campusidletrade_server.entity.vo.notification.NotificationVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.NotificationMapper;
import com.zcpbysj.campusidletrade_server.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    @Override
    public Map<String, Object> getNotifications(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.orderByDesc(Notification::getCreateTime);
        
        Page<Notification> pageResult = page(new Page<>(page, size), wrapper);
        
        List<NotificationVO> list = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Integer unreadCount = baseMapper.countUnread(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageResult.getTotal());
        result.put("unreadCount", unreadCount);
        
        return result;
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        Notification notification = getById(id);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此通知");
        }
        
        notification.setIsRead(1);
        updateById(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        baseMapper.markAllAsRead(userId);
    }

    @Override
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        save(notification);
    }
    
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setType(notification.getType());
        vo.setIsRead(notification.getIsRead() == 1);
        vo.setRelatedId(notification.getRelatedId());
        vo.setCreateTime(notification.getCreateTime());
        return vo;
    }
}

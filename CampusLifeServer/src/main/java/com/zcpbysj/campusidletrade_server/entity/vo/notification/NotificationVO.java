package com.zcpbysj.campusidletrade_server.entity.vo.notification;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知VO
 */
@Data
public class NotificationVO {
    
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 通知类型
     */
    private String type;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 关联ID
     */
    private Long relatedId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

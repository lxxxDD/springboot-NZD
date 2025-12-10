package com.zcpbysj.campusidletrade_server.entity.vo.message;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话VO
 */
@Data
public class ConversationVO {
    
    private Long id;
    
    /**
     * 对方用户ID
     */
    private Long userId;
    
    /**
     * 对方用户名
     */
    private String username;
    
    /**
     * 对方头像
     */
    private String avatar;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
}

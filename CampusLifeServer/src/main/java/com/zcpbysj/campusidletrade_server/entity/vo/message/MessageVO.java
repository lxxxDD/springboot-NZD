package com.zcpbysj.campusidletrade_server.entity.vo.message;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息VO
 */
@Data
public class MessageVO {
    
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 发送者头像
     */
    private String senderAvatar;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 是否是自己发送的
     */
    private Boolean isMine;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

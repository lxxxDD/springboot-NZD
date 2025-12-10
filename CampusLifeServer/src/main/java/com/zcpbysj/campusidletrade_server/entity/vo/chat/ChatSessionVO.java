package com.zcpbysj.campusidletrade_server.entity.vo.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话VO
 */
@Data
public class ChatSessionVO {

    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

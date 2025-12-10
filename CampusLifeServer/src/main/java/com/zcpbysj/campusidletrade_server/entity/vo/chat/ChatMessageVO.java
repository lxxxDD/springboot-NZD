package com.zcpbysj.campusidletrade_server.entity.vo.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息VO
 */
@Data
public class ChatMessageVO {

    private Long id;

    /**
     * 角色: user/assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

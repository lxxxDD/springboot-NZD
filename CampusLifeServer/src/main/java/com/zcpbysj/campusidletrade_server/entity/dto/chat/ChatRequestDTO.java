package com.zcpbysj.campusidletrade_server.entity.dto.chat;

import lombok.Data;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequestDTO {

    /**
     * 消息内容
     */
    private String message;

    /**
     * 会话ID（可选，为空则创建新会话）
     */
    private String sessionId;
}

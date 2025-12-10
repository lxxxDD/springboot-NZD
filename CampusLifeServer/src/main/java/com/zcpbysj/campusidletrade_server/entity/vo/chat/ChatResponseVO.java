package com.zcpbysj.campusidletrade_server.entity.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseVO {

    /**
     * AI回复内容
     */
    private String reply;

    /**
     * 会话ID
     */
    private String sessionId;
}

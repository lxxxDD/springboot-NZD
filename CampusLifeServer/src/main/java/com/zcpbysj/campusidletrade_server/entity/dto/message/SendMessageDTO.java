package com.zcpbysj.campusidletrade_server.entity.dto.message;

import lombok.Data;

/**
 * 发送消息请求DTO
 */
@Data
public class SendMessageDTO {
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型: text/image
     */
    private String type;
}

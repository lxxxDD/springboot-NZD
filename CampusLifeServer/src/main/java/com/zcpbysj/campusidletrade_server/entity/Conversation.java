package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话表
 */
@Data
@TableName("conversations")
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户1 ID
     */
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Long user1Id;

    /**
     * 用户2 ID
     */
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Long user2Id;

    /**
     * 最后一条消息ID
     */
    private Long lastMessageId;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

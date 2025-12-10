package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Message;
import com.zcpbysj.campusidletrade_server.entity.dto.message.SendMessageDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.ConversationVO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.MessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 消息服务接口
 */
public interface IMessageService extends IService<Message> {
    
    /**
     * 获取会话列表
     */
    List<ConversationVO> getConversations(Long userId);
    
    /**
     * 获取或创建会话
     */
    Map<String, Object> getOrCreateConversation(Long userId, Long otherUserId);
    
    /**
     * 获取聊天记录
     */
    PageVO<MessageVO> getMessages(Long conversationId, Long userId, Integer page, Integer size);
    
    /**
     * 发送消息
     */
    Long sendMessage(Long senderId, SendMessageDTO dto);
    
    /**
     * 标记已读
     */
    void markAsRead(Long conversationId, Long userId);
    
    /**
     * 删除会话
     */
    void deleteConversation(Long conversationId, Long userId);
}

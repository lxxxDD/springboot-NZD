package com.zcpbysj.campusidletrade_server.service;

import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatMessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatResponseVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatSessionVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface IChatService {

    /**
     * 发送消息并获取AI回复
     */
    ChatResponseVO chat(Long userId, String message, String sessionId);

    /**
     * 获取聊天历史
     */
    PageVO<ChatMessageVO> getHistory(String sessionId, Integer page, Integer size);

    /**
     * 清除会话历史
     */
    void clearHistory(String sessionId);

    /**
     * 获取用户的会话列表
     */
    List<ChatSessionVO> getSessions(Long userId);

    /**
     * 获取推荐问题
     */
    List<String> getSuggestions();
}

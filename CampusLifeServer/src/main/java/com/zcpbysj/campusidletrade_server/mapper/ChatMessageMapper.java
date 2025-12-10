package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI对话消息Mapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 获取最近的消息记录
     */
    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} ORDER BY create_time DESC LIMIT #{limit}")
    List<ChatMessage> getRecentMessages(@Param("sessionId") String sessionId, @Param("limit") int limit);
}

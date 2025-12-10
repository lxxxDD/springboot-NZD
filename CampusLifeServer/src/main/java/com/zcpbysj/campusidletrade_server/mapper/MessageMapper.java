package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 统计未读消息数
     */
    @Select("SELECT COUNT(*) FROM messages WHERE conversation_id = #{conversationId} AND receiver_id = #{userId} AND is_read = 0")
    Integer countUnread(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    /**
     * 标记会话消息为已读
     */
    @Update("UPDATE messages SET is_read = 1 WHERE conversation_id = #{conversationId} AND receiver_id = #{userId} AND is_read = 0")
    int markAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}

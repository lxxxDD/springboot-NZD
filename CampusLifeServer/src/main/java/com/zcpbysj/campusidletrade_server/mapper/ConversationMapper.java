package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 会话Mapper
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
    
    /**
     * 查找两个用户之间的会话
     */
    @Select("SELECT * FROM conversations WHERE (user1_id = #{user1Id} AND user2_id = #{user2Id}) OR (user1_id = #{user2Id} AND user2_id = #{user1Id}) LIMIT 1")
    Conversation findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}

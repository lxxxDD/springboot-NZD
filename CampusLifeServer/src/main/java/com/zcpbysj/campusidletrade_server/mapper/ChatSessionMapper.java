package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
}

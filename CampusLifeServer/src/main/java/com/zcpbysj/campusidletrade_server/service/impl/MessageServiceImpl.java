package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Conversation;
import com.zcpbysj.campusidletrade_server.entity.Message;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.message.SendMessageDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.ConversationVO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.MessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.ConversationMapper;
import com.zcpbysj.campusidletrade_server.mapper.MessageMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    private final ConversationMapper conversationMapper;
    private final UserMapper userMapper;

    @Override
    public List<ConversationVO> getConversations(Long userId) {
        // 查询用户参与的所有会话
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUser1Id, userId)
               .or()
               .eq(Conversation::getUser2Id, userId);
        wrapper.orderByDesc(Conversation::getLastMessageTime);
        
        List<Conversation> conversations = conversationMapper.selectList(wrapper);
        
        List<ConversationVO> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            ConversationVO vo = new ConversationVO();
            vo.setId(conv.getId());
            
            // 确定对方用户
            Long otherUserId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();
            vo.setUserId(otherUserId);
            
            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser != null) {
                vo.setUsername(otherUser.getUsername());
                vo.setAvatar(otherUser.getAvatar());
            }
            
            // 获取最后一条消息
            if (conv.getLastMessageId() != null) {
                Message lastMsg = getById(conv.getLastMessageId());
                if (lastMsg != null) {
                    vo.setLastMessage(lastMsg.getContent());
                }
            }
            vo.setLastMessageTime(conv.getLastMessageTime());
            
            // 统计未读消息数
            Integer unreadCount = baseMapper.countUnread(conv.getId(), userId);
            vo.setUnreadCount(unreadCount);
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public PageVO<MessageVO> getMessages(Long conversationId, Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId);
        wrapper.orderByDesc(Message::getCreateTime);
        
        Page<Message> pageResult = page(new Page<>(page, size), wrapper);
        
        List<MessageVO> list = pageResult.getRecords().stream()
                .map(msg -> convertToVO(msg, userId))
                .collect(Collectors.toList());
        
        return PageVO.of(list, pageResult.getTotal(), page, size);
    }

    @Override
    public Map<String, Object> getOrCreateConversation(Long userId, Long otherUserId) {
        // 查找或创建会话
        Conversation conversation = conversationMapper.findByUsers(userId, otherUserId);
        
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser1Id(userId);
            conversation.setUser2Id(otherUserId);
            conversationMapper.insert(conversation);
        }
        
        // 返回会话ID
        Map<String, Object> result = new HashMap<>();
        result.put("conversationId", conversation.getId());
        return result;
    }
    
    @Override
    @Transactional
    public Long sendMessage(Long senderId, SendMessageDTO dto) {
        // 查找或创建会话
        Conversation conversation = conversationMapper.findByUsers(senderId, dto.getReceiverId());
        
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser1Id(senderId);
            conversation.setUser2Id(dto.getReceiverId());
            conversationMapper.insert(conversation);
        }
        
        // 创建消息
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setMessageType(dto.getType() != null ? dto.getType() : "text");
        message.setIsRead(0);
        save(message);
        
        // 更新会话的最后消息
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);
        
        // 推送WebSocket消息
        try {
            MessageVO vo = convertToVO(message, senderId);
            // 注意：这里推送给接收者，所以isMine对于接收者来说应该是false
            vo.setIsMine(false); 
            com.zcpbysj.campusidletrade_server.websocket.WebSocketServer.sendObject(vo, dto.getReceiverId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return message.getId();
    }

    @Override
    public void markAsRead(Long conversationId, Long userId) {
        baseMapper.markAsRead(conversationId, userId);
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("会话不存在");
        }
        
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            throw new RuntimeException("无权删除此会话");
        }
        
        // 删除会话中的所有消息
        remove(new LambdaQueryWrapper<Message>().eq(Message::getConversationId, conversationId));
        
        // 删除会话
        conversationMapper.deleteById(conversationId);
    }
    
    private MessageVO convertToVO(Message message, Long currentUserId) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setSenderId(message.getSenderId());
        vo.setContent(message.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setIsMine(message.getSenderId().equals(currentUserId));
        vo.setCreateTime(message.getCreateTime());
        
        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {
            vo.setSenderName(sender.getUsername());
            vo.setSenderAvatar(sender.getAvatar());
        }
        
        return vo;
    }
}

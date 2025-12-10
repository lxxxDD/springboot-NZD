package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.message.SendMessageDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.ConversationVO;
import com.zcpbysj.campusidletrade_server.entity.vo.message.MessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.IMessageService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息接口
 */
@Tag(name = "消息模块")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> getConversations(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<ConversationVO> list = messageService.getConversations(userId);
        return Result.success(list);
    }
    
    @Operation(summary = "获取或创建会话")
    @GetMapping("/conversation")
    public Result<Map<String, Object>> getOrCreateConversation(
            @RequestParam Long otherUserId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Map<String, Object> data = messageService.getOrCreateConversation(userId, otherUserId);
        return Result.success(data);
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/conversations/{id}")
    public Result<PageVO<MessageVO>> getMessages(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        PageVO<MessageVO> vo = messageService.getMessages(id, userId, page, size);
        return Result.success(vo);
    }

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<Map<String, Long>> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody SendMessageDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long messageId = messageService.sendMessage(userId, dto);
        Map<String, Long> data = new HashMap<>();
        data.put("messageId", messageId);
        return Result.success(data);
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read/{conversationId}")
    public Result<Void> markAsRead(
            @PathVariable Long conversationId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        messageService.markAsRead(conversationId, userId);
        return Result.success();
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/conversations/{id}")
    public Result<Void> deleteConversation(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        messageService.deleteConversation(id, userId);
        return Result.success();
    }
}

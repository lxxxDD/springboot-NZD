package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.chat.ChatRequestDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatMessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatResponseVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatSessionVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.IChatService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI聊天接口
 */
@Tag(name = "AI助手模块")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<ChatResponseVO> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatRequestDTO request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        ChatResponseVO response = chatService.chat(userId, request.getMessage(), request.getSessionId());
        return Result.success(response);
    }

    @Operation(summary = "获取聊天历史")
    @GetMapping("/history")
    public Result<PageVO<ChatMessageVO>> getHistory(
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        PageVO<ChatMessageVO> history = chatService.getHistory(sessionId, page, size);
        return Result.success(history);
    }

    @Operation(summary = "清除会话历史")
    @DeleteMapping("/history/{sessionId}")
    public Result<Void> clearHistory(@PathVariable String sessionId) {
        chatService.clearHistory(sessionId);
        return Result.success();
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/sessions")
    public Result<List<ChatSessionVO>> getSessions(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<ChatSessionVO> sessions = chatService.getSessions(userId);
        return Result.success(sessions);
    }

    @Operation(summary = "获取推荐问题")
    @GetMapping("/suggestions")
    public Result<List<String>> getSuggestions() {
        List<String> suggestions = chatService.getSuggestions();
        return Result.success(suggestions);
    }
}

package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.entity.CampusKnowledge;
import com.zcpbysj.campusidletrade_server.entity.ChatMessage;
import com.zcpbysj.campusidletrade_server.entity.ChatSession;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatMessageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatResponseVO;
import com.zcpbysj.campusidletrade_server.entity.vo.chat.ChatSessionVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.CampusKnowledgeMapper;
import com.zcpbysj.campusidletrade_server.mapper.ChatMessageMapper;
import com.zcpbysj.campusidletrade_server.mapper.ChatSessionMapper;
import com.zcpbysj.campusidletrade_server.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现类
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;
    private final CampusKnowledgeMapper knowledgeMapper;
    
    @Value("${ai.qwen.api-key:}")
    private String apiKey;
    
    @Value("${ai.qwen.enabled:false}")
    private boolean aiEnabled;

    @Override
    @Transactional
    public ChatResponseVO chat(Long userId, String message, String sessionId) {
        // 1. 创建或获取会话
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            ChatSession session = new ChatSession();
            session.setSessionId(sessionId);
            session.setUserId(userId);
            session.setTitle(message.length() > 20 ? message.substring(0, 20) + "..." : message);
            sessionMapper.insert(session);
        }
        
        // 2. 保存用户消息
        saveMessage(sessionId, "user", message);
        
        // 3. 获取AI回复
        String reply;
        if (aiEnabled && apiKey != null && !apiKey.isEmpty()) {
            // 调用AI API
            reply = callAiApi(sessionId, message);
        } else {
            // 使用知识库回复
            reply = getKnowledgeReply(message);
        }
        
        // 4. 保存AI回复
        saveMessage(sessionId, "assistant", reply);
        
        return new ChatResponseVO(reply, sessionId);
    }

    @Override
    public PageVO<ChatMessageVO> getHistory(String sessionId, Integer page, Integer size) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId);
        wrapper.orderByAsc(ChatMessage::getCreateTime);
        
        Page<ChatMessage> pageResult = messageMapper.selectPage(new Page<>(page, size), wrapper);
        
        List<ChatMessageVO> list = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageVO.of(list, pageResult.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void clearHistory(String sessionId) {
        // 删除消息
        messageMapper.delete(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId));
        // 删除会话
        sessionMapper.delete(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getSessionId, sessionId));
    }

    @Override
    public List<ChatSessionVO> getSessions(Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUserId, userId);
        wrapper.orderByDesc(ChatSession::getUpdateTime);
        
        return sessionMapper.selectList(wrapper).stream()
                .map(this::convertToSessionVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSuggestions() {
        return Arrays.asList(
            "图书馆开放时间是什么？",
            "食堂今天有什么好吃的？",
            "如何提交报修申请？",
            "最近有什么校园活动？",
            "如何使用二手市场？"
        );
    }
    
    private void saveMessage(String sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);
    }
    
    private String getKnowledgeReply(String question) {
        // 从知识库搜索答案
        List<CampusKnowledge> results = knowledgeMapper.searchByKeywords(question);
        
        if (!results.isEmpty()) {
            // 返回最匹配的答案
            return results.get(0).getAnswer();
        }
        
        // 默认回复
        return "抱歉，我暂时无法回答这个问题。您可以尝试问我关于图书馆、食堂、报修或校园活动的问题哦~ 😊";
    }
    
    private String callAiApi(String sessionId, String message) {
        try {
            // 获取历史消息
            List<ChatMessage> history = messageMapper.getRecentMessages(sessionId, 10);
            
            // 搜索知识库
            String knowledgeAnswer = searchKnowledge(message);
            
            // 构建系统提示
            String systemPrompt = buildSystemPrompt(knowledgeAnswer);
            
            // 构建请求
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", "qwen-turbo");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            
            // 添加历史消息（倒序变正序）
            Collections.reverse(history);
            for (ChatMessage msg : history) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
            messages.add(Map.of("role", "user", "content", message));
            
            body.put("messages", messages);
            
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
                entity,
                Map.class
            );
            
            // 解析响应
            Map responseBody = response.getBody();
            if (responseBody != null) {
                List<Map> choices = (List<Map>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = choices.get(0);
                    Map messageMap = (Map) choice.get("message");
                    return (String) messageMap.get("content");
                }
            }
            
            return getKnowledgeReply(message);
        } catch (Exception e) {
            // AI调用失败，使用知识库回复
            return getKnowledgeReply(message);
        }
    }
    
    private String buildSystemPrompt(String knowledgeAnswer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是 Parker，一个友好的校园生活助手。");
        prompt.append("你可以帮助学生解答关于图书馆、食堂、报修、活动等校园生活问题。");
        prompt.append("请用简洁友好的语气回答，必要时可以使用表情符号。");
        
        if (knowledgeAnswer != null && !knowledgeAnswer.isEmpty()) {
            prompt.append("\n\n参考信息：").append(knowledgeAnswer);
        }
        
        return prompt.toString();
    }
    
    private String searchKnowledge(String question) {
        List<CampusKnowledge> results = knowledgeMapper.searchByKeywords(question);
        if (results.isEmpty()) {
            return null;
        }
        return results.stream()
            .map(k -> k.getQuestion() + ": " + k.getAnswer())
            .collect(Collectors.joining("\n"));
    }
    
    private ChatMessageVO convertToVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setRole(message.getRole());
        vo.setContent(message.getContent());
        vo.setCreateTime(message.getCreateTime());
        return vo;
    }
    
    private ChatSessionVO convertToSessionVO(ChatSession session) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setSessionId(session.getSessionId());
        vo.setTitle(session.getTitle());
        vo.setCreateTime(session.getCreateTime());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }
}

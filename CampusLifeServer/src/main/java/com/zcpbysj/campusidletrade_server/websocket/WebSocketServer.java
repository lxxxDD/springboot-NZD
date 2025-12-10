package com.zcpbysj.campusidletrade_server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 */
@Slf4j
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        sessionMap.put(userId, session);
        log.info("用户{}连接成功，当前在线人数：{}", userId, sessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        sessionMap.remove(userId);
        log.info("用户{}连接关闭，当前在线人数：{}", userId, sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") Long userId) {
        log.info("收到用户{}的消息：{}", userId, message);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误");
        error.printStackTrace();
    }

    /**
     * 发送消息给指定用户
     *
     * @param message 消息内容
     * @param userId  用户ID
     */
    public static void sendInfo(String message, Long userId) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("推送消息给用户{}成功：{}", userId, message);
            } catch (IOException e) {
                log.error("推送消息给用户{}失败", userId, e);
            }
        } else {
            log.info("用户{}不在线，消息未推送", userId);
        }
    }
    
    /**
     * 发送对象消息给指定用户
     */
    public static void sendObject(Object object, Long userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 处理Java 8时间类型
            objectMapper.findAndRegisterModules();
            String json = objectMapper.writeValueAsString(object);
            sendInfo(json, userId);
        } catch (Exception e) {
            log.error("序列化消息失败", e);
        }
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isOnline(Long userId) {
        return sessionMap.containsKey(userId) && sessionMap.get(userId).isOpen();
    }
}

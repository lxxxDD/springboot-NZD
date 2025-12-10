package com.zcpbysj.campusidletrade_server.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获所有未处理的异常，返回统一格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 检查是否是JWT/Token相关异常
    private boolean isTokenException(String message) {
        if (message == null) return false;
        String lowerMsg = message.toLowerCase();
        return lowerMsg.contains("jwt") || lowerMsg.contains("token") || 
               lowerMsg.contains("令牌") || lowerMsg.contains("认证") ||
               lowerMsg.contains("expired") || lowerMsg.contains("过期");
    }

    // 捕获所有运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        e.printStackTrace(); // 日志打印异常栈（生产环境可改为日志框架记录）
        
        String message = e.getMessage();
        // JWT/Token相关异常返回401
        if (isTokenException(message)) {
            return Result.fail(401, "登录已过期，请重新登录");
        }
        
        return Result.fail(500, "系统异常：" + message);
    }

    // 可继续添加其他异常的捕获方法，如自定义业务异常、参数异常等
    // @ExceptionHandler(BusinessException.class)
    // public Result<?> handleBusinessException(BusinessException e) {
    //     return Result.fail(e.getCode(), e.getMessage());
    // }
}
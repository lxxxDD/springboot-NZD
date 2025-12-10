package com.zcpbysj.campusidletrade_server.common;

import lombok.Data;

/**
 * 统一接口响应类
 * 所有接口都返回该格式
 */
@Data
public class Result<T> {
    // 状态码（自定义，如 200=成功，400=参数错误，500=系统异常）
    private Integer code;
    // 是否成功
    private Boolean success;
    // 响应数据（泛型支持任意类型）
    private T data;
    // 提示信息（成功/失败原因）
    private String message;

    // 私有构造，通过静态方法创建实例
    private Result() {}

    // ========== 成功响应（重载方法，支持不同场景） ==========
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setMessage("操作成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setData(data);
        result.setMessage("操作成功");
        return result;
    }

    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    // ========== 失败响应（重载方法，支持不同场景） ==========
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        return fail(500, message); // 默认系统异常码 500
    }

    // ========== 错误响应（别名方法） ==========
    public static <T> Result<T> error(String message) {
        return fail(400, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return fail(code, message);
    }
}
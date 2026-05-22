package com.lz.server.model;

/**
 * @Author: lz
 * @Date: 2026/4/20 15:15
 * @Description: 统一响应结果封装类
 */
public class Result<T> {
    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功结果（无数据）
     *
     * @param <T> 泛型类型
     * @return Result<Void> 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 返回成功结果（带数据）
     *
     * @param data 响应数据
     * @param <T> 泛型类型
     * @return Result<T> 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 返回错误结果
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     泛型类型
     * @return Result<T> 错误响应
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public void setCode(int code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
}

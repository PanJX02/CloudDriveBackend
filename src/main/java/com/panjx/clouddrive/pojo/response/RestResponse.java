package com.panjx.clouddrive.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private int code;
    private String message;
    private T data;

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200, "success", data);
    }

    /**
     * 成功响应（无数据）
     * @return 响应对象
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<>(200, "success", null);
    }

    /**
     * 错误响应
     * @param code 错误码
     * @param message 错误消息
     * @return 响应对象
     */
    public static <T> RestResponse<T> error(int code, String message) {
        return new RestResponse<>(code, message, null);
    }
} 
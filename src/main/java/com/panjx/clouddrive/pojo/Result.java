package com.panjx.clouddrive.pojo;

import lombok.Data;

/**
 * 后端统一返回结果
 */
@Data
public class Result {

    private Integer code; //编码：1成功，0为失败
    private String message; //错误信息
    private Object data; //数据

    //成功返回结果(无参)
    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.message = "success";
        return result;
    }

    //成功返回结果
    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 1;
        result.message = "success";
        return result;
    }

    //失败返回结果
    public static Result error(String msg) {
        Result result = new Result();
        result.message = msg;
        result.code = 0;
        return result;
    }

}

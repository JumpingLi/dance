package com.champion.dance.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with payment-service
 * User: jiangping.li
 * Date: 2017/9/11
 * Time: 18:45
 */
@Data
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = -9168177275266965586L;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    public static final int NO_PERMISSION = 2;

    private String msg = "success";
    private int code = SUCCESS;
    private boolean success = true;
    private T data;

    /**
     * 无data调用
     * @param msg
     */
    public ResultBean(String msg, boolean success) {
        super();
        this.msg = msg;
        this.success = success;
    }

    /**
     * 无权限调用
     */
    public ResultBean() {
        super();
        this.msg = "无权限调用接口";
        this.code = NO_PERMISSION;
        this.success = false;
    }

    /**
     * 有date成功调用
     * @param data
     */
    public ResultBean(T data) {
        super();
        this.data = data;
    }

    /**
     * 调用发生错误
     * @param e
     */
    public ResultBean(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL;
        this.success = false;
    }
}

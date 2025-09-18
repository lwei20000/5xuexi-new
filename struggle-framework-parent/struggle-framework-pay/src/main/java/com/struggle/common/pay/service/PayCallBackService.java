package com.struggle.common.pay.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface PayCallBackService {

    /**
     * 支付成功回调
     * map：支付回调参数
     * async：是否异步接口
     */
    void paySuccess(Map<String, String> map, boolean async, HttpServletRequest request, HttpServletResponse response);

    /**
     * 支付失败回调
     * map：支付回调参数
     * async：是否异步接口
     */
    void payFail(Map<String, String> map,boolean async, HttpServletRequest request, HttpServletResponse response);
}

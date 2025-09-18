package com.struggle.common.pay.service;

import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.pay.controller.alipay.AliPayService;
import com.struggle.common.pay.entity.PayOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class PayService {

    @Resource
    private AliPayService aliPayService;

    /**
     * 支付
     * @param request
     * @param response
     * @param payOrder
     */
    public void pay(HttpServletRequest request, HttpServletResponse response, PayOrder payOrder){
        String userAgent = request.getHeader("User-Agent");
        if(payOrder.getPayType().equals("zfb")){
            if (userAgent != null && userAgent.contains("Mobile")) {
                // 移动端访问
                aliPayService.wapPay(response,payOrder);
            } else {
                aliPayService.pcPay(response,payOrder);
            }
        }else{
            throw new BusinessException("支付类型错误");
        }
    }
}

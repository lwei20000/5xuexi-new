package com.struggle.common.pay.controller.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ijpay.alipay.AliPayApi;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.pay.entity.PayConfigBean;
import com.struggle.common.pay.service.PayCallBackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 用户
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name = "阿里云支付回调接口")
@RestController
@RequestMapping("/api")
public class AliPayController extends BaseController {
    @Resource
    private PayConfigBean payConfigBean;
    @Resource
    private PayCallBackService payCallBackService;
    /**
     * 同步回调接口
     * @param request
     * @return
     */
    @RequestMapping("/aliPay/return_url")
    @NoLoginCheck
    public void returnUrl(HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            // 获取支付宝GET过来反馈信息
            Map<String, String> map = AliPayApi.toMap(request);
            boolean verifyResult = AlipaySignature.rsaCheckV1(map, payConfigBean.getAlipay().getPublicKey(), "UTF-8", "RSA2");
            if (verifyResult) {
                payCallBackService.paySuccess(map,false,request,response);
            }else{
                payCallBackService.payFail(map,false,request,response);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步回调接口
     * @param request
     * @return
     */
    @RequestMapping("/aliPay/notify_url")
    @NoLoginCheck
    public void notifyUrl(HttpServletResponse response,HttpServletRequest request) {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = AliPayApi.toMap(request);
            boolean verifyResult = AlipaySignature.rsaCheckV1(params,  payConfigBean.getAlipay().getPublicKey(), "UTF-8", "RSA2");
            if (verifyResult) {
                payCallBackService.paySuccess(params,true,request,response);
            }else{
                payCallBackService.payFail(params,true,request,response);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }
}

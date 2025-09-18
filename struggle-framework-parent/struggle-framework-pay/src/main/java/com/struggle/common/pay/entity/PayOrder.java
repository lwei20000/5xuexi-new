package com.struggle.common.pay.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "支付订单对象")
public class PayOrder {

    @Schema(description = "支付类型：zfb、wx")
    private String  payType;

    @Schema(description = "订单编号")
    private String  orderCode;

    @Schema(description = "价格")
    private Double  price;

    @Schema(description = "标题")
    private String  title;

    @Schema(description = "内容")
    private String  body;

    @Schema(description = "图片")
    private String picture;

    @Schema(description = "返回Url")
    private String  quitUrl;

    @Schema(description = "公用参数json UrlEncode后传入")
    private String passbackParams;

}

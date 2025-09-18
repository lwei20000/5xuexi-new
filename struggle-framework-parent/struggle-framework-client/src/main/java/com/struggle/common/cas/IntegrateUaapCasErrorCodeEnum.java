package com.struggle.common.cas;

import lombok.Getter;
import lombok.Setter;

public enum IntegrateUaapCasErrorCodeEnum {

    NO_USER_PRINCIPAL_IN_REQUEST(1000, "request中没有找到用户信息，请尝试重新访问登录地址"),
    CAN_NOT_FIND_USER_FROM_LOCAL_DATABASE(1001, "认证返回了用户信息,但是在本地数据库中未找到对应用户"),
    FIND_MULTIPLE_USER_FROM_LOCAL_DATABASE(1002, "认证返回的用户信息在本地数据库中找到多个对应用户"),
    TICKET_IS_INVALID(1003, "交换token时票据非法");

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    IntegrateUaapCasErrorCodeEnum(int code, String msg) {

        this.code = code;
        this.msg = msg;
    }
}

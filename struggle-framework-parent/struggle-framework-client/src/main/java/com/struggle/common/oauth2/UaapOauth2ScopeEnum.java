package com.struggle.common.oauth2;

import lombok.Getter;
import lombok.Setter;

public enum UaapOauth2ScopeEnum {

    simple("只返回登录名，不需要用户确认"),
    detail("返回详细信息，需要用户确认");

    @Getter
    @Setter
    private String description;

    UaapOauth2ScopeEnum(String description) {

        this.description = description;
    }
}

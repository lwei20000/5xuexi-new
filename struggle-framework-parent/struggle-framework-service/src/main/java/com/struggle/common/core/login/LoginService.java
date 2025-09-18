package com.struggle.common.core.login;

import com.struggle.common.system.result.LoginResult;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    /**
     * 认证后的用户登录
     * @param username
     * @param request
     * @return
     */
    LoginResult login(String username, HttpServletRequest request);
}

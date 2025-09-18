package com.struggle.common.oauth2.controller;

import com.struggle.common.cas.util.CasClientUtils;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.login.LoginService;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.oauth2.UaapOauth2ScopeEnum;
import com.struggle.common.oauth2.pojo.IntegrateUaapOauth2Properties;
import com.struggle.common.oauth2.service.AbstractIntegrateService;
import com.struggle.common.system.result.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping("/api/integrate/uaap/oauth2")
@Tag(name = "认证oauth2集成")
public class IntegrateUaapOauth2Controller extends BaseController {
    @Resource
    private IntegrateUaapOauth2Properties properties;
    @Resource
    private AbstractIntegrateService abstractIntegrateService;
    @Resource
    private LoginService loginService;

   @Operation(description= "获得认证的url")
    @GetMapping("/authorize-url")
    @NoLoginCheck
    public ApiResult<String> getAuthorizeUrl(@RequestParam(value = CasClientUtils.TO_FRONT_PARAM_NAME) String next, @RequestParam(value ="responseType",defaultValue = "code") String responseType, @RequestParam(value ="scope",defaultValue = "simple") UaapOauth2ScopeEnum scope) throws Exception {

        return success("成功", properties.getAuthorizeUrlPrefix() + "?client_id=" + URLEncoder.encode(properties.getClientId(), "UTF-8") + "&redirect_uri=" +  URLEncoder.encode(CasClientUtils.generateFrontUrl(properties.getFrontUrl(), next),"UTF-8") + "&response_type=" + URLEncoder.encode(responseType, "UTF-8") + "&scope=" + URLEncoder.encode(scope.name(), "UTF-8") + "&state=xxx");
    }

   @Operation(description= "通过code进行登录")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/login")
    @NoLoginCheck
    public ApiResult<?> login(@RequestParam(value ="responseType",defaultValue = "code")String responseType,@RequestParam(value ="token") String token, HttpServletRequest request)throws Exception {
        String username = null;
        if(responseType.equals("code")){
            String access_token = abstractIntegrateService.getAccessTokenByCode(properties.getFrontUrl(), token);
            Map<String, Object> userByAccessToken = abstractIntegrateService.getUserProfileByAccessToken(access_token);
            username = userByAccessToken.get("id").toString();
        }else{
            Map<String, Object> userByAccessToken = abstractIntegrateService.getUserProfileByAccessToken(token);
            username = userByAccessToken.get("id").toString();
        }
        //登录
        if(!StringUtils.hasText(username)){
            throw new BusinessException("认证失败");
        }
        LoginResult login = loginService.login(username, request);
        return success("登录成功", login);
    }
}

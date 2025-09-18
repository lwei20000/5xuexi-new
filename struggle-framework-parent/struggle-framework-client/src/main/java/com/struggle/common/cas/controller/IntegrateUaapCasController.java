package com.struggle.common.cas.controller;


import com.struggle.common.cas.pojo.IntegrateUaapCasProperties;
import com.struggle.common.cas.util.CasClientUtils;
import com.struggle.common.client.authentication.AttributePrincipal;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.login.LoginService;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.result.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@Tag(name = "认证cas集成")
@Slf4j
public class IntegrateUaapCasController extends BaseController {
    @Resource
    private IntegrateUaapCasProperties integrateUaapCasProperties;
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtil redisUtil;
    private static final String CAS_SERVICE_TICKET_TO_TOKEN_PREFIX = "CAS_SERVICE_TICKET_TO_TOKEN";
    /**
     * 发起认证登录
     */
    @Operation(description="使用认证登录")
    @GetMapping("/api/integrate/uaap/cas/to-cas-login")
    @NoLoginCheck
    public void toCasLogin(@RequestParam(value = CasClientUtils.TO_FRONT_PARAM_NAME) String next, @RequestParam(value ="thirdPartyName",required = false) String thirdPartyName, HttpServletResponse response) throws Exception {
        String casLoginUrl = CasClientUtils.generateCasLoginUrl(integrateUaapCasProperties.getServerUrlPrefix(), integrateUaapCasProperties.getClientHostUrl(),integrateUaapCasProperties.getFrontUrl(), next,thirdPartyName);
        response.sendRedirect(casLoginUrl);
    }

    /**
     * 认证登录成功后回调，再转向前端
     */
    @Operation(description="解析cas返回")
    @GetMapping(CasClientUtils.RESOLVE_CAS_RETURN_URI)
    @NoLoginCheck
    public void resolveCasReturn(@RequestParam(value = CasClientUtils.TO_FRONT_PARAM_NAME) String next, @RequestParam(value = "ticket") String ticket, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AttributePrincipal principal = (AttributePrincipal)request.getUserPrincipal();
        //ticket加入redis缓存
        redisUtil.set(this.generateServiceTicket2SessionIdKeyName(ticket),JSONUtil.toJSONString(principal),60L);
        response.sendRedirect(next + "&ticket=" + ticket);
    }

    /**
     * 前端获取登录信息
     */
    @Operation(description="交换token")
    @GetMapping("/api/integrate/uaap/cas/exchange-token")
    @NoLoginCheck
    public ApiResult<?> exchangeToken(@RequestParam(value = "token") String token, HttpServletRequest request) {
        String principalStr = redisUtil.get(this.generateServiceTicket2SessionIdKeyName(token));
        if (!StringUtils.hasText(principalStr)) {
            return fail("认证失败");
        }
        Map<String,Object> map = (Map<String,Object>) JSONUtil.parseObject(principalStr, Map.class);
        //登录
        LoginResult login = loginService.login(map.get("name").toString(), request);
        return success("登录成功", login);
    }

    private String generateServiceTicket2SessionIdKeyName(String ticket) {

        return String.join("@", IntegrateUaapCasController.CAS_SERVICE_TICKET_TO_TOKEN_PREFIX, ticket);
    }
}

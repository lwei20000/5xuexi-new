package com.struggle.common.core.config;

import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.security.JwtSubject;
import com.struggle.common.core.security.JwtUtil;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.system.entity.LoginRecord;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.LoginRecordService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor  implements HandlerInterceptor {

    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LoginRecordService loginRecordService;

    /**
     * 进入控制器之前拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod) handler;
            //获取方法上的Aop注解
            NoLoginCheck annotation = method.getMethod().getAnnotation(NoLoginCheck.class);
            boolean noLoginCheck = false;
            if(annotation != null){
                if(!annotation.loadUser()){
                    return true;
                }
                noLoginCheck = true;
            }
            String access_token = JwtUtil.getAccessToken(request);
            if (StringUtils.hasText(access_token)) {
                try {
                    // 解析token
                    Claims claims = JwtUtil.parseToken(access_token, configProperties.getTokenKey());
                    JwtSubject jwtSubject = JwtUtil.getJwtSubject(claims);
                    //本地线程租户
                    ThreadLocalContextHolder.setTenant(jwtSubject.getTenantId());

                    String key = CommonUtil.stringJoiner(configProperties.getUserPrefix(), access_token);
                    User user = redisUtil.get(key ,User.class);

                    if (user == null) {
                        if(noLoginCheck){
                            return true;
                        }else{
                            throw new BusinessException(Constants.TOKEN_EXPIRED_CODE,Constants.TOKEN_EXPIRED_MSG);
                        }
                    }

                    //本地线程User
                    ThreadLocalContextHolder.setUser(user);

                    // token将要过期签发新token, 防止突然退出登录
                    long expiration = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000 / 60;
                    if (expiration < configProperties.getTokenRefreshTime()) {
                        //新的token
                        String token = JwtUtil.buildToken(jwtSubject, configProperties.getTokenExpireTime(), configProperties.getTokenKey());
                        //加入redis缓存
                        key = CommonUtil.stringJoiner(configProperties.getUserPrefix(),token);
                        redisUtil.set(key,JSONUtil.toJSONString(user),configProperties.getTokenExpireTime());
                        response.addHeader(Constants.TOKEN_HEADER_NAME, token);
                        loginRecordService.saveAsync(user.getUsername(), LoginRecord.TYPE_REFRESH, null, jwtSubject.getTenantId(), request);
                    }
                } catch (ExpiredJwtException e) {
                    if (!noLoginCheck){
                        throw new BusinessException(Constants.TOKEN_EXPIRED_CODE,Constants.TOKEN_EXPIRED_MSG);
                    }
                } catch (Exception e) {
                    if (!noLoginCheck) {
                        throw new BusinessException(Constants.BAD_CREDENTIALS_CODE, Constants.BAD_CREDENTIALS_MSG);
                    }
                }
            } else {
                if (!noLoginCheck) {
                    throw new BusinessException(Constants.UNAUTHENTICATED_CODE, Constants.UNAUTHENTICATED_MSG);
                }
            }
        }
        return true;
    }
    /**
     * 请求处理后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完后，需要清空 ThreadLocalContextHolder 集合数据
        ThreadLocalContextHolder.remove();
    }
}

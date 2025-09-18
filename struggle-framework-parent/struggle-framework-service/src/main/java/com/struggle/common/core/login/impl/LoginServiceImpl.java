package com.struggle.common.core.login.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.login.LoginService;
import com.struggle.common.core.security.JwtSubject;
import com.struggle.common.core.security.JwtUtil;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.system.entity.LoginRecord;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.result.LoginResult;
import com.struggle.common.system.service.LoginRecordService;
import com.struggle.common.system.service.UserService;
import com.struggle.common.system.service.UserTenantService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoginRecordService loginRecordService;

    @Resource
    private UserService userService;

    @Resource
    private UserTenantService userTenantService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ConfigProperties configProperties;

    @Override
    public LoginResult login(String username, HttpServletRequest request){
        User user = userService.getByUsername(username);
        if(user == null){
            throw new BusinessException("未找到用户信息");
        }
        Integer userId = user.getUserId();
        //忽略租户查询用户的所有租户
        List<Tenant> tenants = userTenantService.listByUserId(userId);
        if(CollectionUtils.isEmpty(tenants)){
            throw new BusinessException("未找到用户的租户信息");
        }

        Integer tenantId = null;
        if(user.getCurrentTenantId() != null){
            for (Tenant tenant:tenants){
                if(tenant.getTenantId().equals(user.getCurrentTenantId())){
                    tenantId = user.getCurrentTenantId();
                    break;
                }
            }
        }
        if(tenantId == null){
            tenantId = tenants.get(0).getTenantId();
            userService.update(null, Wrappers.<User>lambdaUpdate().set(User::getCurrentTenantId,tenantId).eq(User::getUserId,user.getUserId()));
        }

        //删除历史缓存
        this.deleted(request);

        ThreadLocalContextHolder.setTenant(tenantId);
        User byIdRel = userService.getByIdRel(userId);
        byIdRel.setCurrentTenantId(tenantId);
        byIdRel.setTenants(tenants);
        loginRecordService.saveAsync(username, LoginRecord.TYPE_LOGIN, null, tenantId, request);
        String access_token = JwtUtil.buildToken(new JwtSubject(username, tenantId , userId), configProperties.getTokenExpireTime(), configProperties.getTokenKey());
        //加入redis缓存
        String key = CommonUtil.stringJoiner(configProperties.getUserPrefix(),access_token);
        redisUtil.set(key, JSONUtil.toJSONString(byIdRel),configProperties.getTokenExpireTime());

        return new LoginResult(access_token, byIdRel);
    }

    private void deleted(HttpServletRequest request){
        String accessToken = JwtUtil.getAccessToken(request);
        if(StringUtils.hasText(accessToken)){
            // 解析token
            try {
                Claims claims = JwtUtil.parseToken(accessToken, configProperties.getTokenKey());
                JwtSubject jwtSubject = JwtUtil.getJwtSubject(claims);
                String _tenantId =  jwtSubject.getTenantId().toString();
                String _userId = jwtSubject.getUserId().toString();
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getMessagePrefix(),_tenantId,_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getAnnouncementPrefix(),_tenantId,_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemMessagePrefix(),_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemAnnouncementPrefix(),_userId));
            }catch (Exception e){
                e.printStackTrace();
            }
            redisUtil.dels(JwtUtil.getAccessToken(request),true);
        }
    }
}

package com.struggle.common.core.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.system.entity.OperationRecord;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.OperationRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * 操作日志记录
 *
 * @author EleAdmin
 * @since 2020-03-21 16:58:16:05
 */
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationRecordService operationRecordService;

    @Resource
    private ConfigProperties configProperties;

    // 用于记录请求耗时
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(com.struggle.common.core.annotation.OperationLog)")
    public void operationLog() {
    }

    @Before("operationLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "operationLog()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, ApiResult result) {
        saveLog(joinPoint, result, null);
    }

    @AfterThrowing(value = "operationLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        saveLog(joinPoint, null, e);
    }

    /**
     * 保存操作记录
     */
    private void saveLog(JoinPoint joinPoint, ApiResult result, Exception e) {
        OperationRecord record = new OperationRecord();
        // 记录操作耗时
        if (startTime.get() != null) {
            record.setSpendTime(System.currentTimeMillis() - startTime.get());
        }
        // 记录当前登录用户id、租户id
        User user = ThreadLocalContextHolder.getUser();
        if (user != null) {
            record.setUserId(user.getUserId());
            record.setTenantId(ThreadLocalContextHolder.getTenant());
        }else{
            record.setTenantId(configProperties.getDefaultTenant());
        }
        // 记录请求地址、请求方式、ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attributes == null ? null : attributes.getRequest());
        if (request != null) {
            record.setUrl(request.getRequestURI());
            record.setRequestMethod(request.getMethod());
            UserAgent ua = UserAgentUtil.parse(ServletUtil.getHeaderIgnoreCase(request, "User-Agent"));
            if (ua != null) {
                if (ua.getPlatform() != null) {
                    record.setOs(ua.getPlatform().toString());
                }
                if (ua.getOs() != null) {
                    record.setDevice(ua.getOs().toString());
                }
                if (ua.getBrowser() != null) {
                    record.setBrowser(ua.getBrowser().toString());
                }
            }
            record.setIp(ServletUtil.getClientIP(request));
        }
        // 记录异常信息
        if (e != null) {
            record.setStatus(1);
            record.setError(e.toString());
        }
        // 记录模块名、操作功能、请求方法、请求参数、返回结果
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        record.setMethod(CommonUtil.stringJoiner((CharSequence)".",joinPoint.getTarget().getClass().getName(),signature.getName()));
        Method method = signature.getMethod();
        if (method != null) {
            OperationLog ol = method.getAnnotation(OperationLog.class);
            if (ol != null) {
                // 记录操作功能
                record.setDescription(getDescription(method, ol));
                // 记录操作模块
                record.setModule(getModule(joinPoint, ol));
               // 记录操作方法
                record.setMethod(getMethod(joinPoint,signature,method));
                // 记录备注
                if (StrUtil.isNotEmpty(ol.comments())) {
                    record.setComments(ol.comments());
                }
                // 记录请求参数
                if (ol.param() && request != null) {
                    record.setParams(getParams(joinPoint, request));
                }
                // 记录请求结果
                if (ol.result() && result != null) {
                    //日志不需要保存返回的date
                    ApiResult _result = new ApiResult<>();
                    _result.setCode(result.getCode());
                    _result.setMessage(result.getMessage());
                    _result.setError(result.getError());
                    record.setResult(JSONUtil.toJSONString(_result));
                }
            }
        }
        operationRecordService.saveAsync(record);
    }


    /**
     * 获取请求参数
     *
     * @param joinPoint JoinPoint
     * @param request   HttpServletRequest
     * @return String
     */
    private String getParams(JoinPoint joinPoint, HttpServletRequest request) {
        String params;
        Map<String, String> paramsMap = ServletUtil.getParamMap(request);
        if (paramsMap.keySet().size() > 0) {
            params = JSONUtil.toJSONString(paramsMap);
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object arg : joinPoint.getArgs()) {
                if (ObjectUtil.isNull(arg)
                        || arg instanceof MultipartFile
                        || arg instanceof HttpServletRequest
                        || arg instanceof HttpServletResponse) {
                    continue;
                }
                sb.append(JSONUtil.toJSONString(arg)).append(" ");
            }
            params = sb.toString();
        }
        return params;
    }

    /**
     * 获取操作方法
     *
     * @param joinPoint JoinPoint
     * @return String
     */
    private String getMethod(JoinPoint joinPoint,MethodSignature signature, Method method) {
        Operation ao = method.getAnnotation(Operation.class);
        if (ao != null && StrUtil.isNotEmpty(ao.method())) {
            return CommonUtil.stringJoiner((CharSequence)".",joinPoint.getTarget().getClass().getName(),ao.method());
        }
        return CommonUtil.stringJoiner((CharSequence)".",joinPoint.getTarget().getClass().getName(),signature.getName());
    }

    /**
     * 获取操作模块
     *
     * @param joinPoint JoinPoint
     * @param ol        OperationLog
     * @return String
     */
    private String getModule(JoinPoint joinPoint, OperationLog ol) {
        if (StrUtil.isNotEmpty(ol.module())) {
            return ol.module();
        }

        Tag tag = joinPoint.getTarget().getClass().getAnnotation(Tag.class);
        if (tag != null && tag.name() != null) {
            return tag.name();
        }
        return null;
    }

    /**
     * 获取操作功能
     *
     * @param method Method
     * @param ol     OperationLog
     * @return String
     */
    private String getDescription(Method method, OperationLog ol) {
        if (StrUtil.isNotEmpty(ol.value())) {
            return ol.value();
        }
        Operation ao = method.getAnnotation(Operation.class);
        if (ao != null && StrUtil.isNotEmpty(ao.description())) {
            return ao.description();
        }
        return null;
    }

}

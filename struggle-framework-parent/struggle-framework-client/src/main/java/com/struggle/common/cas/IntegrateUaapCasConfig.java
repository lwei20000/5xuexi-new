package com.struggle.common.cas;

import com.struggle.common.cas.pojo.IntegrateUaapCasProperties;
import com.struggle.common.client.session.SingleSignOutFilter;
import com.struggle.common.client.session.SingleSignOutHttpSessionListener;
import com.struggle.common.client.util.AssertionThreadLocalFilter;
import com.struggle.common.client.util.HttpServletRequestWrapperFilter;
import com.struggle.common.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class IntegrateUaapCasConfig {

    @Resource
    private IntegrateUaapCasProperties integrateUaapCASProperties;

    @Bean
    public FilterRegistrationBean filterSingleRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SingleSignOutFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean filterValidationRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix",integrateUaapCASProperties.getServerUrlPrefix());
        initParameters.put("serverName", integrateUaapCASProperties.getClientHostUrl());
        initParameters.put("redirectAfterValidation", "false");
        initParameters.put("autoSetPort", "false");
        // initParameters.put("useSession", "false");
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean filterWrapperRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestWrapperFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 设定加载的顺序
        registration.setOrder(4);
        return registration;
    }

    @Bean
    public FilterRegistrationBean assertionThreadLocalFilter() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AssertionThreadLocalFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 设定加载的顺序
        registration.setOrder(5);
        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<EventListener> singleSignOutListenerRegistration() {
        ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<EventListener>();
        registrationBean.setListener(new SingleSignOutHttpSessionListener());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

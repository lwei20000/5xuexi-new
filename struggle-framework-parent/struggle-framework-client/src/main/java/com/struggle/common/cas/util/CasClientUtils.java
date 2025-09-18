package com.struggle.common.cas.util;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CasClientUtils {
    private static final String LOGIN_CONTEXT = "/login";
    private static final String FRONT_LOAD_PAGE_PARAM_NAME = "from";
    public static final String TO_FRONT_PARAM_NAME = "next";
    public static final String RESOLVE_CAS_RETURN_URI = "/api/integrate/uaap/cas/resolve-cas-return";

    public static String generateCasLoginUrl(String casServerUrlPrefix, String clientHostUrl, String frontUrl, String next,String thirdPartyName) throws UnsupportedEncodingException {
        String addstr = LOGIN_CONTEXT;
        if(StringUtils.hasText(thirdPartyName)){
            addstr = "/oauth/login/"+thirdPartyName;
        }
        StringBuffer stringBuffer = new StringBuffer().append(casServerUrlPrefix).append(addstr).append("?service=").append(URLEncoder.encode(CasClientUtils.generateResolveCasReturnUrl(clientHostUrl, frontUrl, next), "UTF-8"));
        return stringBuffer.toString();
    }
    public static String generateResolveCasReturnUrl(String clientHostUrl, String frontUrl, String next) throws UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer().append(clientHostUrl).append(RESOLVE_CAS_RETURN_URI).append("?").append(TO_FRONT_PARAM_NAME).append("=").append(URLEncoder.encode(generateFrontUrl(frontUrl,next), "UTF-8"));
        return stringBuffer.toString();
    }
    public static String generateFrontUrl(String frontUrl,String next) throws UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer().append(frontUrl).append("?").append(FRONT_LOAD_PAGE_PARAM_NAME).append("=").append(URLEncoder.encode(next, "UTF-8"));
        return stringBuffer.toString();
    }
}

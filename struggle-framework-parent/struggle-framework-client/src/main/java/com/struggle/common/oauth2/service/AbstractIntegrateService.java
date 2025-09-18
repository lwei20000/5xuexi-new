package com.struggle.common.oauth2.service;

import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.oauth2.pojo.IntegrateUaapOauth2Properties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.Map;

@Service
public class AbstractIntegrateService {

    @Resource
    private IntegrateUaapOauth2Properties properties;

    private final static RestTemplate restTemplate = new RestTemplate();

    public String getAccessTokenByCode(String redirectUrl, String code) throws Exception {

        try {
            MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
            postParameters.add("client_id", properties.getClientId());
            postParameters.add("client_secret", properties.getClientSecret());
            postParameters.add("grant_type", "authorization_code");
            postParameters.add("redirect_uri", redirectUrl);
            postParameters.add("code", URLDecoder.decode(code, "UTF-8"));
            postParameters.add("returnType", "json");

            String body = restTemplate.postForObject(properties.getAccessTokenUrlPrefix(), postParameters, String.class);
            if(StringUtils.hasText(body)){
                if (body.startsWith("{") && body.endsWith("}")) {
                    Map<String,String> map = JSONUtil.parseObject(body, Map.class);
                    if (map.containsKey("error")) {
                        throw new BusinessException("获取access_token失败："+map.get("error"));
                    }
                    return map.get("access_token");
                }else{
                    if(body.indexOf("error=")>-1){
                        throw new BusinessException("获取access_token失败："+body);
                    }
                    body = body.replace("access_token=","");
                    return body.substring(0,body.lastIndexOf("&")) ;
                }
            }else{
                throw new BusinessException("获取access_token失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("获取access_token失败");
        }
    }

    public Map<String, Object> getUserProfileByAccessToken(String accessToken)  throws Exception{
        Map<String, Object> map = null;
        try {
            map = restTemplate.getForObject(properties.getProfileUrlPrefix()+"?access_token="+accessToken,Map.class);
            if (map.containsKey("error")) {
                throw new BusinessException(map.get("error")+"");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("获取access_token失败");
        }

        return map;
    }
}

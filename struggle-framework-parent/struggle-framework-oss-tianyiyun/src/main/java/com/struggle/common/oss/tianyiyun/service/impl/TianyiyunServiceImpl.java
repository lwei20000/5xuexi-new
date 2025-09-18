package com.struggle.common.oss.tianyiyun.service.impl;


import cn.ctyun.test.TestConfig;
import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.oss.ConfProperties;
import com.struggle.common.oss.tianyiyun.config.TianyiyunToken;
import com.struggle.common.oss.tianyiyun.service.TianyiyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class TianyiyunServiceImpl implements TianyiyunService {

    @Resource
    private ConfProperties confProperties;

    @Override
    public TianyiyunToken getToken() {
        TianyiyunToken tianyitunToken = new TianyiyunToken();
        try {
            ClientConfiguration cc = new ClientConfiguration();
            cc.setMaxErrorRetry(3);
            cc.setConnectionTimeout(60*1000);
            cc.setSocketTimeout(60*1000) ;
            cc.setProtocol(Protocol.HTTPS);
            System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY,"true");
            AmazonIdentityManagementClient stsClient = new
                    AmazonIdentityManagementClient(new AWSCredentials() {
                public String getAWSAccessKeyId() {
                    return confProperties.getAccessKey();
                }
                public String getAWSSecretKey() {
                    return confProperties.getSecretKey();
                }
            }, cc);
            stsClient.setEndpoint(TestConfig.OOS_ENDPOINT_ACCESS);

            GetSessionTokenRequest request = new GetSessionTokenRequest();
            request.setDurationSeconds(3600);// 过期时间，单位秒
            GetSessionTokenResult sessionTokenResult = stsClient.getSessionToken(request);
            Credentials sessionCredentials = sessionTokenResult.getCredentials();

            tianyitunToken.setAk(sessionCredentials.getAccessKeyId());
            tianyitunToken.setSk(sessionCredentials.getSecretAccessKey());
            tianyitunToken.setTk(sessionCredentials.getSessionToken());
            tianyitunToken.setDomainPrefix(confProperties.getDomainPrefix());
            tianyitunToken.setDomain(confProperties.getDomain());
            tianyitunToken.setBucket(confProperties.getBucket());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tianyitunToken;
    }

    private  AmazonS3 getAmazonS3(){
        ClientConfiguration clientConfig = new ClientConfiguration();
        //设置连接的超时时间，单位毫秒
        clientConfig.setConnectionTimeout(30*1000);
        //设置 socket 超时时间，单位毫秒
        clientConfig.setSocketTimeout(30*1000) ;
        clientConfig.setProtocol(Protocol.HTTP); //设置 http
        //设置 V4 签名算法中负载是否参与签名，关于签名部分请参看《OOS 开发者文档》
        S3ClientOptions options = new S3ClientOptions();
        options.setPayloadSigningEnabled(true);
        // 创建 client
        AmazonS3 oosClient = new AmazonS3Client(
                new PropertiesCredentials(confProperties.getAccessKey(),
                        confProperties.getSecretKey()),clientConfig);
        // 设置 endpoint
        oosClient.setEndpoint("oos-cn.ctyunapi.cn");
        if(StringUtils.hasText(confProperties.getEndpoint())) {
            oosClient.setEndpoint(confProperties.getEndpoint());
        }
        //设置选项
        oosClient.setS3ClientOptions(options);
        return oosClient;
    }

    /**
     * @Desecription: 删除文件
     */
    @Override
    public boolean delete(String key) {
        String _key = key.substring(key.lastIndexOf('/') + 1);
        AmazonS3 ossClient = this.getAmazonS3();
        boolean exist = ossClient.doesBucketExist(confProperties.getBucket());
        if (exist) {
            try {
                ossClient.deleteObject(confProperties.getBucket(), _key);
            } catch (Exception e) {
                throw new BusinessException("[天翼云附件删除异常]");
            } finally {
                if (ossClient != null) {
                    try {
                        ((AmazonS3Client) ossClient).shutdown();
                    } catch (AmazonClientException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    /**
     * 上传文件
     * @param file
     */
    @Override
    public String upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            AmazonS3 ossClient = this.getAmazonS3();
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");

            String name = CommonUtil.stringJoiner(CommonUtil.randomUUID16(),originalFilename);
            try {
                objectMetadata.setContentLength(inputStream.available());
                // 上传文件
                log.info("开始上传文件到oss");
                log.info("bucket: " + confProperties.getBucket());
                log.info("path: " + name);
                log.info("inputStream: " + inputStream);
                log.info("objectMetadata: " + objectMetadata);
                ossClient.putObject(confProperties.getBucket(), name, inputStream, objectMetadata);

                return  confProperties.getDomainPrefix() +confProperties.getBucket() +'.' + confProperties.getDomain() + "/" + name;
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("[天翼云附件上传异常]" + e.getMessage());
            } finally {
                if (ossClient != null) {
                    ((AmazonS3Client) ossClient).shutdown();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException("[天翼云附件上传异常]" + e.getMessage());
        }
    }
    /**
     * 上传文件
     */
    @Override
    public String upload(InputStream inputStream,String originalFilename){
        try {
            AmazonS3 ossClient = this.getAmazonS3();
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");

            String name = CommonUtil.stringJoiner(CommonUtil.randomUUID16(),originalFilename);
            try {
                objectMetadata.setContentLength(inputStream.available());
                // 上传文件
                log.info("开始上传文件到oss");
                log.info("bucket: " + confProperties.getBucket());
                log.info("path: " + name);
                log.info("inputStream: " + inputStream);
                log.info("objectMetadata: " + objectMetadata);
                ossClient.putObject(confProperties.getBucket(), name, inputStream, objectMetadata);

                return  confProperties.getDomainPrefix() +confProperties.getBucket() +'.' + confProperties.getDomain() + "/" + name;
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("[天翼云附件上传异常]" + e.getMessage());
            } finally {
                if (ossClient != null) {
                    ((AmazonS3Client) ossClient).shutdown();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException("[天翼云附件上传异常]" + e.getMessage());
        }
    }
}

package com.struggle.common.oss.qiniu.service.impl;


import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.oss.ConfProperties;
import com.struggle.common.oss.qiniu.config.QiniuToken;
import com.struggle.common.oss.qiniu.service.QiniuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;

@Service
public class QiniuServiceImpl implements QiniuService {

    @Resource
    private  ConfProperties confProperties;

    @Override
    public QiniuToken getToken() {
        StringMap putPolicy = new StringMap();
        Auth auth = Auth.create(confProperties.getAccessKey(), confProperties.getSecretKey());
        String tempBucket = confProperties.getBucket();
        String token = auth.uploadToken(tempBucket, null, 3600L, putPolicy, true);
        QiniuToken qiniuToken = new QiniuToken();
        qiniuToken.setToken(token);
        qiniuToken.setDomainPrefix(confProperties.getDomainPrefix());
        qiniuToken.setDomain(confProperties.getDomain());
        return qiniuToken;
    }

    private Configuration getConfiguration(){
        Zone z = Zone.autoZone();
        if(!StringUtils.hasText(confProperties.getEndpoint())){
            switch (confProperties.getEndpoint()){
                case "z0": z = Zone.zone0(); break;
                case "z1": z = Zone.zone1(); break;
                case "z2": z = Zone.zone2(); break;
            }
        }
        return new Configuration(z);
    }

    /**
     * 删除文件
     *
     * @param key
     */
    @Override
    public void delete(String key) {
        String _key = key.substring(key.lastIndexOf('/') + 1);
        Auth auth = Auth.create(confProperties.getAccessKey(), confProperties.getSecretKey());
        //实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth, this.getConfiguration());
        try {
            //调用delete方法移动文件
            bucketManager.delete(confProperties.getBucket(), _key);
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new BusinessException("[七牛云附件删除异常]" + e.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @param file (File对象)
     * @return 文件路径
     */
    @Override
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        UploadManager uploadManager = new UploadManager(this.getConfiguration());
        try {
            InputStream inputStream = file.getInputStream();
            // 构建token
            String token = this.getToken().getToken();
            if (!StringUtils.hasText(token)) {
                throw new BusinessException("获取token失败");
            }
            //构建文件名
            // 其中UUID.randomUUID()用于生成唯一的文件名
            String name = CommonUtil.stringJoiner(CommonUtil.randomUUID16(),originalFilename);
            //上传操作
            uploadManager.put(inputStream, name, token, null, null);

            return confProperties.getDomainPrefix() + confProperties.getDomain() + "/" + name;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("[七牛云附件上传异常]" + e.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @return 文件路径
     */
    @Override
    public String upload(InputStream inputStream ,String originalFilename) {
        UploadManager uploadManager = new UploadManager(this.getConfiguration());
        try {
            // 构建token
            String token = this.getToken().getToken();
            if (!StringUtils.hasText(token)) {
                throw new BusinessException("获取token失败");
            }
            //构建文件名
            // 其中UUID.randomUUID()用于生成唯一的文件名
            String name = CommonUtil.stringJoiner(CommonUtil.randomUUID16(),originalFilename);
            //上传操作
            uploadManager.put(inputStream, name, token, null, null);

            return confProperties.getDomainPrefix() + confProperties.getDomain() + "/" + name;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("[七牛云附件上传异常]" + e.getMessage());
        }
    }
}

package com.struggle.common.oss.tianyiyun.service;


import com.struggle.common.oss.tianyiyun.config.TianyiyunToken;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface TianyiyunService {

    TianyiyunToken getToken();

    /**
     * @Desecription: 删除文件
     */
    boolean delete(String key);

    /**
     * 上传文件
     * @param file
     */
    String upload(MultipartFile file);
    /**
     * 上传文件
     */
    String upload(InputStream inputStream,String originalFilename);
}

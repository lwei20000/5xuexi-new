package com.struggle.common.oss.qiniu.service;


import com.struggle.common.oss.qiniu.config.QiniuToken;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface QiniuService {

    QiniuToken getToken();
    /**
     * 删除文件
     *
     * @param key
     */
    void delete(String key);

    /**
     * 文件上传
     *
     * @param file (File对象)
     * @return 文件路径
     */
    String upload(MultipartFile file);

    /**
     * 文件上传
     *
     * @return 文件路径
     */
    String upload(InputStream inputStream ,String originalFilename);
}

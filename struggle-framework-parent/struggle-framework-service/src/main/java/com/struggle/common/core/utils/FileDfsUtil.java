package com.struggle.common.core.utils;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.oss.qiniu.service.QiniuService;
import com.struggle.common.oss.tianyiyun.service.TianyiyunService;
import com.struggle.common.system.entity.FileRecord;
import com.struggle.common.system.mapper.FileRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class FileDfsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDfsUtil.class);
    @Resource
    private FastFileStorageClient storageClient;
    @Resource
    private FdfsWebServer fdfsWebServer;
    @Resource
    private FileRecordMapper fileRecordMapper;

    @Autowired(required = false)
    private QiniuService qiniuService;

    @Autowired(required = false)
    private TianyiyunService tianyiyunService;

    @Resource
    private ConfigProperties configProperties;

    /**
     * 上传文件
     */
    public FileRecord upload(MultipartFile multipartFile,Integer userId,String uploadType) throws Exception{
        if(!StringUtils.hasText(uploadType)){
            uploadType = configProperties.getDefaultUpload();
        }
        String  path = "";
        String originalFilename = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        if(uploadType.equals("fastdfs")){
            StorePath storePath = null;
            if(isImage(multipartFile.getContentType())){
                storePath = this.storageClient.uploadImageAndCrtThumbImage(
                        multipartFile.getInputStream(),
                        multipartFile.getSize(),originalFilename , null);
            }else{
                storePath = this.storageClient.uploadFile(
                        multipartFile.getInputStream(),
                        multipartFile.getSize(),originalFilename , null);
            }
            path = fdfsWebServer.getWebServerUrl()+"/"+storePath.getFullPath();
        }else if(uploadType.equals("qiniu")){
            path = qiniuService.upload(multipartFile);
        }else if(uploadType.equals("tianyiyun")){
            path = tianyiyunService.upload(multipartFile);
        }else{
            throw new BusinessException("上传类型错误");
        }

        FileRecord result = new FileRecord();
        result.setCreateUserId(userId);
        result.setName(multipartFile.getOriginalFilename());
        result.setLength(multipartFile.getSize());
        result.setPath(path);
        result.setContentType(multipartFile.getContentType());
        result.setUploadType(uploadType);
        fileRecordMapper.insert(result);
        
        return result;
    }
    /**
     * 上传文件
     */
    public FileRecord upload(String name,byte[] array,Integer userId,String contentType,String uploadType) throws Exception{
        if(!StringUtils.hasText(uploadType)){
            uploadType = configProperties.getDefaultUpload();
        }
        String  path = "";
        InputStream inputStream = new ByteArrayInputStream(array);
        String originalFilename = name.substring(name.lastIndexOf(".") + 1);
        if(uploadType.equals("fastdfs")){
            StorePath storePath = this.storageClient.uploadFile(inputStream, array.length,originalFilename, null);
            path = fdfsWebServer.getWebServerUrl()+"/"+storePath.getFullPath();
        }else if(uploadType.equals("qiniu")){
            path = qiniuService.upload(inputStream,name);
        }else if(uploadType.equals("tianyiyun")){
            path = tianyiyunService.upload(inputStream,name);
        }else{
            throw new BusinessException("上传类型错误");
        }

        FileRecord result = new FileRecord();
        result.setCreateUserId(userId);
        result.setName(name);
        result.setLength(Long.valueOf(array.length));
        result.setPath(path);
        result.setContentType(contentType);
        result.setUploadType(uploadType);
        fileRecordMapper.insert(result);
        return result;
    }
    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl,String uploadType) {
        if (StringUtils.hasText(fileUrl) && StringUtils.hasText(uploadType)) {
            try {
                if(uploadType.equals("fastdfs")){
                    StorePath storePath = StorePath.parseFromUrl(fileUrl);
                    storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
                }else if(uploadType.equals("qiniu")){
                    qiniuService.delete(fileUrl);
                }else if(uploadType.equals("tianyiyun")){
                    tianyiyunService.delete(fileUrl);
                }
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    /**
     * 判断文件是否是图片类型
     *
     * @param contentType 文件类型
     * @return boolean
     */
    public  boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}

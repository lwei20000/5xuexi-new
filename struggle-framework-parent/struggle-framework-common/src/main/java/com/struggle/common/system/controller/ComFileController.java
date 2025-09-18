package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.FileRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件上传下载控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:24
 */
@Tag(name = "COM文件上传下载",description = "ComFileController")
@RestController
@RequestMapping("/api/file")
public class ComFileController extends BaseController {
    @Resource
    private FileDfsUtil fileDfsUtil;

    @AuthorityAnnotation("sys:auth:user")
    @Operation(method="upload",description="上传文件")
    @PostMapping("/upload")
    public ApiResult<FileRecord> upload(@RequestParam(value = "file")MultipartFile file,@RequestParam(value = "uploadType",required = false) String uploadType) {
        FileRecord result = null;
        try {
            result = fileDfsUtil.upload(file,getLoginUserId(),uploadType);
            return success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return fail("上传失败", result).setError(e.toString());
        }
    }
}

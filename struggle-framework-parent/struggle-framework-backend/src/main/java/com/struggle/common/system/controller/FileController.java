package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.FileRecord;
import com.struggle.common.system.param.FileRecordParam;
import com.struggle.common.system.service.FileRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件上传下载控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:24
 */
@Tag(name = "文件上传下载",description = "FileController")
@RestController
@RequestMapping("/api/file")
public class FileController extends BaseController {
    @Resource
    private FileRecordService fileRecordService;
    @Resource
    private FileDfsUtil fileDfsUtil;

    @AuthorityAnnotation("sys:file:remove:ef2a3d1a4f994ac496d24386e1e3eb1f")
    @OperationLog
    @Operation(method = "remove",description="删除文件")
    @PostMapping("/remove/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        FileRecord record = fileRecordService.getById(id);
        if (fileRecordService.removeById(id)) {
            fileDfsUtil.deleteFile(record.getPath(),record.getUploadType());
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:file:remove:ef2a3d1a4f994ac496d24386e1e3eb1f")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除文件")
    @PostMapping("/remove/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        List<FileRecord> fileRecords = fileRecordService.listByIds(ids);
        if (fileRecordService.removeByIds(ids)) {
            for (FileRecord record : fileRecords) {
                fileDfsUtil.deleteFile(record.getPath(),record.getUploadType());
            }
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:file:list:68d44c503c984a19b40ade012a10d44c")
    @OperationLog
    @Operation(method = "page",description="分页查询文件")
    @GetMapping("/page")
    public ApiResult<PageResult<FileRecord>> page(FileRecordParam param, HttpServletRequest request) {
        PageResult<FileRecord> result = fileRecordService.pageRel(param);
        return success(result);
    }

    @AuthorityAnnotation("sys:file:list:68d44c503c984a19b40ade012a10d44c")
    @OperationLog
    @Operation(method = "list",description="查询全部文件")
    @GetMapping("/list")
    public ApiResult<List<FileRecord>> list(FileRecordParam param, HttpServletRequest request) {
        List<FileRecord> records = fileRecordService.listRel(param);
        return success(records);
    }
}

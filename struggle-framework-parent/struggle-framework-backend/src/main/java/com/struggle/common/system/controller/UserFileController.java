package com.struggle.common.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.UserFile;
import com.struggle.common.system.param.UserFileParam;
import com.struggle.common.system.service.UserFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户文件控制器
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
@Tag(name = "用户文件管理",description = "UserFileController")
@RestController
@RequestMapping("/api/system/user-file")
public class UserFileController extends BaseController {
    @Resource
    private UserFileService userFileService;
    @Resource
    private FileDfsUtil fileDfsUtil;

    @OperationLog
    @Operation(method = "page",description="分页查询用户文件")
    @GetMapping("/page")
    public ApiResult<PageResult<UserFile>> page(UserFileParam param) {
        param.setUserId(getLoginUserId());
        PageResult<UserFile> result = userFileService.pageRel(param);
        return success(result.getList(), result.getCount());
    }

    @OperationLog
    @Operation(method = "list",description="查询全部用户文件")
    @GetMapping()
    public ApiResult<List<UserFile>> list(UserFileParam param) {
        param.setUserId(getLoginUserId());
        List<UserFile> records = userFileService.listRel(param);
        return success(records);
    }

    @AuthorityAnnotation("sys:auth:user")
    @OperationLog
    @Operation(method = "save",description="添加用户文件")
    @PostMapping()
    public ApiResult<?> save(@RequestBody UserFile userFile) {
        userFile.setUserId(getLoginUserId());
        if (userFile.getIsDirectory() != null && userFile.getIsDirectory().equals(1)) {
            if (userFileService.count(new LambdaQueryWrapper<UserFile>().eq(UserFile::getName, userFile.getName()).eq(UserFile::getParentId, userFile.getParentId()).eq(UserFile::getUserId, userFile.getUserId())) > 0) {
                return fail("文件夹名称已存在");
            }
        }
        if (userFileService.save(userFile)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:auth:user")
    @OperationLog
    @Operation(method = "update",description="修改用户文件")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody UserFile userFile) {
        Integer loginUserId = getLoginUserId();
        UserFile old = userFileService.getById(userFile.getId());
        UserFile entity = new UserFile();
        if (StrUtil.isNotBlank(userFile.getName())) {
            entity.setName(userFile.getName());
        }
        if (userFile.getParentId() != null) {
            entity.setParentId(userFile.getParentId());
        }
        if (!old.getUserId().equals(loginUserId) || (entity.getName() == null && entity.getParentId() == null)) {
            return fail("修改失败");
        }
        if(userFile.getParentId() !=null && old.getIsDirectory() == 1 && userFile.getParentId() !=0){
            if(userFile.getId().equals(userFile.getParentId())){
                throw new BusinessException("上级文件夹不能是当前文件夹");
            }
            List<Integer> childrenIds = new ArrayList<>();
            userFileService.getUserFileChildrenIds(userFile,childrenIds);
            if(childrenIds.contains(userFile.getParentId())){
                throw new BusinessException("上级文件夹不能是当前文件夹的子文件");
            }
        }
        if (old.getIsDirectory() != null && old.getIsDirectory().equals(1)) {
            if (userFileService.count(new LambdaQueryWrapper<UserFile>()
                    .eq(UserFile::getName, entity.getName() == null ? old.getName() : entity.getName())
                    .eq(UserFile::getParentId, entity.getParentId() == null ? old.getParentId() : entity.getParentId())
                    .eq(UserFile::getUserId, loginUserId)
                    .ne(UserFile::getId, old.getId())) > 0) {
                return fail("文件夹名称已存在");
            }
        }
        if (userFileService.update(entity, new LambdaUpdateWrapper<UserFile>()
                .eq(UserFile::getId, userFile.getId())
                .eq(UserFile::getUserId, loginUserId))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:auth:user")
    @OperationLog
    @Operation(method = "remove",description="删除用户文件")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        long count = userFileService.count(new LambdaQueryWrapper<UserFile>().eq(UserFile::getParentId, id).eq(UserFile::getUserId, getLoginUserId()));
        if(count>0){
            return fail("先删除子级文件");
        }
        List<UserFile> list = userFileService.list(new LambdaUpdateWrapper<UserFile>().eq(UserFile::getId, id).eq(UserFile::getUserId, getLoginUserId()));
        if (userFileService.remove(new LambdaUpdateWrapper<UserFile>().eq(UserFile::getId, id).eq(UserFile::getUserId, getLoginUserId()))) {
            //删除源文件
            for (UserFile record : list) {
                fileDfsUtil.deleteFile(record.getPath(),record.getUploadType());
            }
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:auth:user")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除用户文件")
    @PostMapping("/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        long count = userFileService.count(new LambdaQueryWrapper<UserFile>().in(UserFile::getParentId, ids).eq(UserFile::getUserId, getLoginUserId()));
        if(count>0){
            return fail("先删除子级文件");
        }
        List<UserFile> list = userFileService.list(new LambdaUpdateWrapper<UserFile>().in(UserFile::getId, ids).eq(UserFile::getUserId, getLoginUserId()));
        if (userFileService.remove(new LambdaUpdateWrapper<UserFile>().in(UserFile::getId, ids).eq(UserFile::getUserId, getLoginUserId()))) {
            //删除源文件
            for (UserFile record : list) {
                fileDfsUtil.deleteFile(record.getPath(),record.getUploadType());
            }
            return success("删除成功");
        }
        return fail("删除失败");
    }
}

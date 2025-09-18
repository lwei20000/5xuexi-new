package com.struggle.common.base.controller;

import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.base.service.UserCourseService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.core.web.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户与课程关系
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "用户与课程关系管理",description = "UserCourseController")
@RestController
@RequestMapping("/api/base/user-course")
public class UserCourseController extends BaseController {
    @Resource
    private UserCourseService userCourseService;

    @AuthorityAnnotation(value = "base:userCourse:list:bf30a126b7cf4916a7fcd8137f1cebdf",permission = true)
    @OperationLog
    @Operation(method="page",description="分页查询用户与课程关系")
    @GetMapping("/page")
    public ApiResult<PageResult<UserCourse>> page(UserCourseParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        PageResult<UserCourse> userCoursePageResult = userCourseService.pageRel(param);
        return success(userCoursePageResult);
    }

    @AuthorityAnnotation(value = "base:userCourse:list:bf30a126b7cf4916a7fcd8137f1cebdf",permission = true)
    @OperationLog
    @Operation(method="list",description="查询用户与课程关系")
    @PostMapping("/list")
    public ApiResult<List<UserCourse>> list(@RequestBody UserCourseParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        List<UserCourse> userCoursePageResult = userCourseService.listRel(param,false);
        return success(userCoursePageResult);
    }

    @Operation(method="dataExport",description="数据导出")
    @AuthorityAnnotation(value="base:userCourse:list:bf30a126b7cf4916a7fcd8137f1cebdf",permission = true)
    @RequestMapping(method = RequestMethod.GET, value = "/dataExport")
    public ApiResult<?> dataExport(HttpServletRequest request, HttpServletResponse response,UserCourseParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        userCourseService.dataExport(response,param);
        return null;
    }

    @AuthorityAnnotation(value="base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd",permission = true)
    @OperationLog
    @Operation(method="updateScore",description="批量修改平时成绩")
    @PostMapping("/updateScore")
    public ApiResult<?> updateScore(@RequestBody UserCourseParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        userCourseService.updateScore(param);
        return success("更新成功");
    }

    @AuthorityAnnotation(value="base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd",permission = true)
    @OperationLog
    @Operation(method="update",description="修改用户成绩")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody UserCourse param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        if(param.getId() ==null){
           return fail("用户课程Id 不能为空");
        }
        if(param.getLearingScore() ==null){
            return fail("学习成绩不能为空");
        }
        if(0>param.getLearingScore() || param.getLearingScore()>100){
            return fail("学习成绩只能0-100之间");
        }
        if(param.getExamScore() ==null){
            return fail("考试成绩不能为空");
        }
        if(-1>param.getExamScore() || param.getExamScore()>100){
            return fail("考试成绩只能-1-100之间");
        }
        userCourseService.updateExamScore(param,permission.getOrgIds());
        return success("更新成功");
    }

    /**
     * excel导入
     */
    @AuthorityAnnotation(value="base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd",permission = true)
    @Operation(method="importBatch",description="导入")
    @PostMapping("/scoreImport")
    public ApiResult<?> scoreImport(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        UserCourseParam param = new UserCourseParam();
        param.setOrgIds(permission.getOrgIds());
        return userCourseService.scoreImportBatch(file,param,request,response);
    }

    @Operation(method="templateExport",description="模板导出")
    @AuthorityAnnotation("base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd")
    @RequestMapping(method = RequestMethod.GET, value = "/scoreTemplateExport")
    public ApiResult<?> scoreTemplateExport(HttpServletRequest request, HttpServletResponse response) {
        userCourseService.scoreTemplateExport(response);
        return null;
    }
}

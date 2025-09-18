package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.entity.UserLearning;
import com.struggle.common.base.entity.UserMajor;
import com.struggle.common.base.param.UserMajorParam;
import com.struggle.common.base.service.MajorService;
import com.struggle.common.base.service.UserCourseService;
import com.struggle.common.base.service.UserLearningService;
import com.struggle.common.base.service.UserMajorService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.core.world.ImgObject;
import com.struggle.common.core.world.WordOperation;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.TenantService;
import com.struggle.common.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.Files;

/**
 * 用户与专业关系控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "用户与专业关系管理",description = "UserMajorController")
@RestController
@RequestMapping("/api/base/user-major")
public class UserMajorController extends BaseController {
    @Resource
    private UserMajorService userMajorService;
    @Resource
    private UserCourseService userCourseService;
    @Resource
    private UserLearningService userLearningService;
    @Resource
    private MajorService majorService;
    @Resource
    private TenantService tenantService;
    @Resource
    private UserService userService;

    @AuthorityAnnotation(value = "base:userMajor:list:bf30a126b7cf4916a7fcd8137f1cebdf",permission = true)
    @OperationLog
    @Operation(method="page",description="分页查询用户与专业关系")
    @GetMapping("/page")
    public ApiResult<PageResult<UserMajor>> page(UserMajorParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        // 使用关联查询
        param.setOrgIds(permission.getOrgIds());
        return success(userMajorService.pageRel(param));
    }

    @AuthorityAnnotation(value ="base:userMajor:list:bf30a126b7cf4916a7fcd8137f1cebdf",permission = true)
    @OperationLog
    @Operation(method="list",description="查询全部用户与专业关系")
    @GetMapping()
    public ApiResult<List<UserMajor>> list(UserMajorParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        // 使用关联查询
        param.setOrgIds(permission.getOrgIds());
        return success(userMajorService.listRel(param));
    }

    @AuthorityAnnotation(value = "base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95",permission = true)
    @OperationLog
    @Operation(method="save",description="添加用户与专业关系")
    @PostMapping()
    public ApiResult<?> save(@RequestBody UserMajor studentMajor) {
        if(studentMajor.getOrgId()==null){
            throw new BusinessException("站点不能为空");
        }
        CommonUtil.verifyingPermission(ThreadLocalContextHolder.getPermission(),studentMajor.getOrgId(),null);
        if (userMajorService.saveUserMajor(studentMajor)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation(value = "base:userMajor:update:452847da630c40519612451da8ed520e",permission = true)
    @OperationLog
    @Operation(method="update",description="修改用户与专业关系")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody UserMajor studentMajor) {
        if(studentMajor.getOrgId() == null){
            return fail("站点不能为空");
        }
        UserMajor byId = userMajorService.getById(studentMajor.getId());
        if(byId == null){
            return fail("数据未找到");
        }
        CommonUtil.verifyingPermission(ThreadLocalContextHolder.getPermission(),byId.getOrgId(),null);
        if (userMajorService.updateUserMajor(studentMajor)){
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation(value = "base:userMajor:remove:be7616c3-8a2e4ac2a137d87dfdf22662",permission = true)
    @OperationLog
    @Operation(method="remove",description="删除用户与专业关系")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        UserMajor byId = userMajorService.getById(id);
        if(byId == null){
            return success("删除成功");
        }
        CommonUtil.verifyingPermission(ThreadLocalContextHolder.getPermission(),byId.getOrgId(),null);
        if (userMajorService.removeById(id)) {
            //删除用户课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId,byId.getUserId()).eq(UserCourse::getMajorId,id));
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getUserId, byId.getUserId()).eq(UserLearning::getMajorId, id));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation(value = "base:userMajor:remove:be7616c3-8a2e4ac2a137d87dfdf22662",permission = true)
    @OperationLog
    @Operation(method="removeBatch",description="批量删除用户与专业关系")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        List<UserMajor> userMajors = userMajorService.listByIds(ids);
        List<Integer> userIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userMajors)){
            for(UserMajor userMajor: userMajors){
                userIds.add(userMajor.getUserId());
                CommonUtil.verifyingPermission(ThreadLocalContextHolder.getPermission(),userMajor.getOrgId(),null);
            }
        }
        if (userMajorService.removeByIds(ids)) {
            //删除用户课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().in(UserCourse::getUserId,userIds).in(UserCourse::getMajorId,ids));
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().in(UserLearning::getUserId,userIds).in(UserLearning::getMajorId, ids));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    /**
     * excel导入
     */
    @AuthorityAnnotation("base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95")
    @Operation(method="importBatch",description="导入")
    @PostMapping("/import")
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        return userMajorService.importBatch(file,request,response);
    }

    @Operation(method="templateExport",description="模板导出")
    @AuthorityAnnotation("base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95")
    @RequestMapping(method = RequestMethod.GET, value = "/templateExport")
    public ApiResult<?> templateExport(HttpServletRequest request, HttpServletResponse response) {
        userMajorService.templateExport(response);
        return null;
    }

    /**
     * excel导入论文成绩
     */
    @AuthorityAnnotation("base:userMajor:thesisImport:adaaf9bf787c432d991ca6fd0bf6fc95")
    @Operation(method="importBatch",description="导入")
    @PostMapping("/thesisImport")
    public ApiResult<?> thesisImport(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        return userMajorService.thesisImportBatch(file,request,response);
    }

    @Operation(method="templateExport",description="模板导出")
    //@AuthorityAnnotation("base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95")
    @RequestMapping(method = RequestMethod.GET, value = "/thesisTemplateExport")
    public ApiResult<?> thesisTemplateExport(HttpServletRequest request, HttpServletResponse response) {
        userMajorService.thesisTemplateExport(response);
        return null;
    }

    @AuthorityAnnotation("base:userMajor:downloadRegister:bf30a126b7cf4916a7fcd8137f1cebdf")
    @Operation(method="downloadRegister",description="批量下载登记表")
    @RequestMapping(method = RequestMethod.POST, value = "/downloadRegister")
    public ResponseEntity<org.springframework.core.io.Resource> downloadRegister(@RequestBody Map<String, List<Long>> request) {
        List<Long> userMajorIds = request.get("userMajorIds");

        // 生成登记表文件
        File file = generateRegistrationFile(userMajorIds);

        // 返回文件流
        org.springframework.core.io.Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registration_list.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @AuthorityAnnotation("base:userMajor:downloadEnrollment:bf30a126b7cf4916a7fcd8137f1cebdf")
    @Operation(method="downloadEnrollment",description="批量下载学籍表")
    @RequestMapping(method = RequestMethod.POST, value = "/downloadEnrollment")
    public ResponseEntity<org.springframework.core.io.Resource> downloadEnrollment(@RequestBody Map<String, List<Long>> request) {
        List<Long> userMajorIds = request.get("userMajorIds");

        // 生成学籍表文件
        File file = generateEnrollmentFile(userMajorIds);

        // 返回文件流
        org.springframework.core.io.Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enrollment_list.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private File generateRegistrationFile(List<Long> userMajorIds) {
        // 生成登记表文件逻辑
        // 例如：查询数据库，生成 Excel 或 PDF 文件
        // 将多个文件打包为 ZIP
        File zipFile = new File("registration_list.zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Long userMajorId : userMajorIds) {
                File file = generateSingleRegistrationFile(userMajorId);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                Files.copy(file.toPath(), zipOut);
                zipOut.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    private File generateEnrollmentFile(List<Long> userMajorIds) {
        // 生成登记表文件逻辑
        // 例如：查询数据库，生成 Excel 或 PDF 文件
        // 将多个文件打包为 ZIP
        File zipFile = new File("enrollment_list.zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Long id : userMajorIds) {
                File file = generateSingleEnrollmentFile(id);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                Files.copy(file.toPath(), zipOut);
                zipOut.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    private File generateSingleRegistrationFile(Long userMajorId) throws IOException {
        UserMajor userMajor = userMajorService.getById(userMajorId.intValue());
        User loginUser = userService.getById(userMajor.getUserId());
        WordOperation wordOperation = userMajorService.generateSingleRegistrationFile(userMajor.getMajorId(),loginUser);
        return wordOperation.getProcessedFile("学生登记表_" + loginUser.getRealname() +loginUser.getIdCard(), ".doc");
    }

    private File generateSingleEnrollmentFile(Long userMajorId) throws IOException {
        UserMajor userMajor = userMajorService.getById(userMajorId.intValue());
        User loginUser = userService.getById(userMajor.getUserId());
        WordOperation wordOperation = userMajorService.generateSingleEnrollmentFile(userMajor.getMajorId(),loginUser);
        return wordOperation.getProcessedFile("学生学籍表_" + loginUser.getRealname() +loginUser.getIdCard(), ".doc");
    }


}

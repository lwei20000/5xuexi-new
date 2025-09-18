package com.struggle.common.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.*;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.param.SysFeedbackParam;
import com.struggle.common.system.param.UserParam;
import com.struggle.common.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 用户控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:41
 */
@Tag(name = "用户管理",description = "UserController")
@RestController
@RequestMapping("/api/system/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private UserOrgService userOrgService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private OrgLeaderService orgLeaderService;
    @Resource
    private ConfigProperties configProperties;

    @AuthorityAnnotation(value = "sys:admin:platform:34b7457d1d6344f1a424720a8ec72d7c1")
    @OperationLog
    @Operation(method = "admin_page",description="分页查询用户")
    @GetMapping("/admin_page")
    public ApiResult<PageResult<User>> admin_page(UserParam param) {
        PageParam<User, UserParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        QueryWrapper<User> wrapper = page.getWrapper();
        wrapper.select("user_id,username,realname,phone,email,id_card");
        IPage<User> pageList = userService.page(page, page.getWrapper());
        return success(new PageResult<>(pageList.getRecords(), page.getTotal()));
    }

    @AuthorityAnnotation(value = "sys:user:list:24b7457d1d7343f1a424720a8ec77d7c",permission = true)
    @OperationLog
    @Operation(method = "page",description="分页查询租户用户")
    @GetMapping("/page")
    public ApiResult<PageResult<User>> page(UserParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        //增加机构范围
        param.setOrgIds(permission.getOrgIds());
        return success(userService.pageRel(param,true));
    }

    @AuthorityAnnotation(value = "sys:user:list:24b7457d1d7343f1a424720a8ec77d7c",permission = true)
    @OperationLog
    @Operation(method = "list",description="查询全部租户用户")
    @GetMapping("/list")
    public ApiResult<List<User>> list(UserParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        //增加机构范围
        param.setOrgIds(permission.getOrgIds());
        return success(userService.listRel(param,true));
    }

    @AuthorityAnnotation(value = "sys:user:list:24b7457d1d7343f1a424720a8ec77d7c",permission = true)
    @OperationLog
    @Operation(method = "get",description="根据id查询租户用户")
    @GetMapping("/{id}")
    public ApiResult<User> get(@PathVariable("id") Integer id) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        UserParam param = new UserParam();
        param.setUserId(id);
        param.setOrgIds(permission.getOrgIds());
        List<User> users = userService.listRel(param,true);
        if(!CollectionUtils.isEmpty(users)){
            return success(users.get(0));
        }
        return fail("未找到用户",null);
    }

    @AuthorityAnnotation("sys:user:save:2de4916849144fbf8b4e3a1cfbc60b80")
    @OperationLog
    @Operation(method = "save",description="添加租户用户")
    @PostMapping()
    public ApiResult<?> save(@RequestBody User user) {
        ValidatorUtil._validBean(user, User.UserDefault.class);
        user.setStatus(0);
        user.setPassword(CommonUtil.encodePassword(user.getPassword()));
        if (userService.saveUser(user)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:user:update:f13dbabbd410476193f385a39234da83")
    @OperationLog
    @Operation(method = "update",description="修改租户用户")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody User user) {
        ValidatorUtil._validBean(user,User.UserDefault.class);
        if(user.getUserId().equals(configProperties.getDefaultUser())){
            return fail("不能修改管理员的信息");
        }
        user.setStatus(null);
        user.setUsername(null);
        user.setPassword(null);
        if (userService.updateUser(user)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:user:remove:2dbb3c3c8ed24afd85caae0d320c6d31")
    @OperationLog
    @Operation(method = "remove",description="删除租户用户")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(id.equals(configProperties.getDefaultUser())){
            return fail("不能删除管理员的信息");
        }
        //只删除关联关系，不删除账号
        if (userTenantService.remove(Wrappers.<UserTenant>lambdaQuery().eq(UserTenant::getUserId,id))) {
            //清空机构和角色、主管机构、分组
            userRoleService.remove(new LambdaUpdateWrapper<UserRole>().eq(UserRole::getUserId,id));
            userOrgService.remove(new LambdaUpdateWrapper<UserOrg>().eq(UserOrg::getUserId,id));
            userGroupService.remove(new LambdaUpdateWrapper<UserGroup>().eq(UserGroup::getUserId,id));
            orgLeaderService.remove(new LambdaUpdateWrapper<OrgLeader>().eq(OrgLeader::getUserId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:user:remove:2dbb3c3c8ed24afd85caae0d320c6d31")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除租户用户")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(ids.contains(configProperties.getDefaultUser())){
            return fail("不能删除管理员的信息");
        }
        //只删除关联关系，不删除账号
        if (userTenantService.remove(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getUserId,ids))) {

            //清空机构和角色、主管机构、分组
            userRoleService.remove(new LambdaUpdateWrapper<UserRole>().in(UserRole::getUserId,ids));
            userOrgService.remove(new LambdaUpdateWrapper<UserOrg>().in(UserOrg::getUserId,ids));
            userGroupService.remove(new LambdaUpdateWrapper<UserGroup>().in(UserGroup::getUserId,ids));
            orgLeaderService.remove(new LambdaUpdateWrapper<OrgLeader>().in(OrgLeader::getUserId,ids));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:user:update:f13dbabbd410476193f385a39234da83")
    @OperationLog
    @Operation(method = "updateStatus",description="修改用户状态")
    @PostMapping("/status")
    public ApiResult<?> updateStatus(@RequestBody User user) {
        if (user.getUserId() == null || user.getStatus() == null || !Arrays.asList(0, 1).contains(user.getStatus())) {
            return fail("参数不正确");
        }
        if(user.getUserId().equals(configProperties.getDefaultUser())){
            return fail("不能修改管理员的状态");
        }
        if (userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, user.getUserId())
                .set(User::getStatus, user.getStatus()))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:user:update:f13dbabbd410476193f385a39234da83")
    @OperationLog
    @Operation(method = "resetPassword",description="重置用户密码")
    @PostMapping("/password")
    public ApiResult<?> resetPassword(@RequestBody User user) {
        if (user.getUserId() == null || StrUtil.isBlank(user.getPassword())) {
            return fail("参数不正确");
        }
        if(user.getUserId().equals(configProperties.getDefaultUser())){
            return fail("不能重置管理员的密码");
        }
        if (userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, user.getUserId())
                .set(User::getPassword, CommonUtil.encodePassword(user.getPassword())))) {
            return success("重置成功");
        } else {
            return fail("重置失败");
        }
    }

    @AuthorityAnnotation("sys:user:save:2de4916849144fbf8b4e3a1cfbc60b80")
    @Operation(method = "importBatch",description="导入租户用户")
    @PostMapping("/import")
    public ApiResult<?> importBatch(@RequestParam(value = "file")MultipartFile file, HttpServletRequest request,HttpServletResponse response) {
        return userService.importBatch(file,request,response);
    }

    @Operation(method = "templateExport",description="模板导出")
    @AuthorityAnnotation("sys:user:save:2de4916849144fbf8b4e3a1cfbc60b80")
    @GetMapping("/templateExport")
    public ApiResult<?> templateExport(HttpServletResponse response) {
        userService.templateExport(response);
        return null;
    }
}

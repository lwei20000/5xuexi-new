package com.struggle.common.system.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.security.JwtSubject;
import com.struggle.common.core.security.JwtUtil;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.AuthoritieMenu;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.security.entity.SysUserProblem;
import com.struggle.common.security.service.SysUserProblemService;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.param.*;
import com.struggle.common.system.result.CaptchaResult;
import com.struggle.common.system.result.LoginResult;
import com.struggle.common.system.service.*;
import com.wf.captcha.SpecCaptcha;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录认证控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:11
 */
@Tag(name = "COM登录认证",description = "ComMainController")
@RestController
@RequestMapping("/api")
public class ComMainController extends BaseController {
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private UserService userService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private LoginRecordService loginRecordService;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private EmailRecordService emailRecordService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SysUserProblemService sysUserProblemService;

    @Operation(method="login",description="用户登录")
    @PostMapping("/login")
    @NoLoginCheck
    public ApiResult<?> login(@RequestBody LoginParam param, HttpServletRequest request) {
        String username = param.getUsername();
        if(!StringUtils.hasText(username)){
            return fail("登录名不能为空", null);
        }
        if(!StringUtils.hasText(param.getPassword())){
            return fail("密码为不能为空", null);
        }
        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"login",username);
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>=10){
            redisUtil.set(numKey,++_num+"",(long)3600);
            return fail("错误达到10次，1小时后再重试", _num);
        }
        if(_num>=3){
            String code = param.getCode();
            if(!StringUtils.hasText(code)){
                redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
                return fail("图形验证码不能为空", _num);
            }
            String uuid = param.getUuid();
            if(!StringUtils.hasText(uuid)){
                redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
                return fail("图形验证码UUID不能为空", _num);
            }
            String _code = redisUtil.get(uuid);
            if(_code == null){
                redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
                return fail("图形验证码已过期", _num);
            }else if(!_code.equals(code.toLowerCase())){
                redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
                return fail("图形验证码错误", _num);
            }
            redisUtil.del(uuid);
        }
        User user = userService.getByUsername(username);
        if(user == null){
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, "账号不存在", configProperties.getDefaultTenant(), request);
            redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
            return fail("账号或密码错误", _num);
        }
        Integer userId = user.getUserId();
        //忽略租户查询用户的所有租户
        List<Tenant> tenants = userTenantService.listByUserId(userId);
        if(CollectionUtils.isEmpty(tenants)){
            return fail("没有租户信息",_num);
        }
        Integer tenantId = null;
        if(user.getCurrentTenantId() != null){
            for (Tenant tenant:tenants){
                if(tenant.getTenantId().equals(user.getCurrentTenantId())){
                    tenantId = user.getCurrentTenantId();
                    break;
                }
            }
        }
        if(tenantId == null){
            tenantId = tenants.get(0).getTenantId();
            userService.update(null, Wrappers.<User>lambdaUpdate().set(User::getCurrentTenantId,tenantId).eq(User::getUserId,user.getUserId()));
        }

        if (!CommonUtil.comparePassword(user.getPassword(), param.getPassword())) {
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, "密码错误", tenantId, request);
            redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
            return fail("账号或密码错误", _num);
        }
        if (!user.getStatus().equals(0)) {
            String message = "账号被冻结";
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, message, tenantId, request);
            redisUtil.set(numKey,++_num+"",configProperties.getCodeExpireTime());
            return fail(message, _num);
        }
        //删除错误次数记录
        redisUtil.del(numKey);
        //删除历史token
        this.deleted(request);
        ThreadLocalContextHolder.setTenant(tenantId);
        User byIdRel = userService.getByIdRel(userId);
        byIdRel.setPassword(null);
        byIdRel.setCurrentTenantId(tenantId);
        byIdRel.setTenants(tenants);
        loginRecordService.saveAsync(username, LoginRecord.TYPE_LOGIN, null, tenantId, request);
        String access_token = JwtUtil.buildToken(new JwtSubject(username, tenantId , userId), configProperties.getTokenExpireTime(), configProperties.getTokenKey());
        //加入redis缓存
        String key = CommonUtil.stringJoiner(configProperties.getUserPrefix(),access_token);

        // 转化出错（注意：byIdRel中生日字段的时区问题会导致转化出错）
        System.out.println("========================================================================================");
        System.out.println(byIdRel.getBirthday());
        System.out.println(JSONUtil.toJSONString(byIdRel));
        System.out.println("========================================================================================");

        redisUtil.set(key,JSONUtil.toJSONString(byIdRel),configProperties.getTokenExpireTime());

        return success("登录成功", new LoginResult(access_token, byIdRel));
    }

    @OperationLog
    @Operation(method="switchTenant",description="切换租户")
    @GetMapping("/switchTenant")
    public ApiResult<LoginResult> switchTenant(@RequestParam(value ="tenantId") Integer tenantId, HttpServletRequest request) {
        User loginUser = getLoginUser();
        Integer _tenantId = null;
        if(loginUser != null){
            List<Tenant> tenants = loginUser.getTenants();
            if(!CollectionUtils.isEmpty(tenants)){
                for(Tenant tenant:tenants){
                    if(tenant.getTenantId().equals(tenantId)){
                        _tenantId = tenantId;
                        break;
                    }
                }
            }
        }
        if(_tenantId == null){
            return fail("未找到租户", null);
        }
        userService.update(null, Wrappers.<User>lambdaUpdate().set(User::getCurrentTenantId,_tenantId).eq(User::getUserId,loginUser.getUserId()));
        ThreadLocalContextHolder.setTenant(_tenantId);
        User byIdRel = userService.getByIdRel(loginUser.getUserId());
        byIdRel.setCurrentTenantId(_tenantId);
        byIdRel.setTenants(loginUser.getTenants());
        loginRecordService.saveAsync(byIdRel.getUsername(), LoginRecord.TYPE_LOGIN, null, _tenantId, request);
        // 签发token
        String access_token = JwtUtil.buildToken(new JwtSubject(byIdRel.getUsername(), _tenantId , byIdRel.getUserId()), configProperties.getTokenExpireTime(), configProperties.getTokenKey());
        //加入redis缓存
        String key = CommonUtil.stringJoiner(configProperties.getUserPrefix(),access_token);
        redisUtil.set(key,JSONUtil.toJSONString(byIdRel),configProperties.getTokenExpireTime());
        return success("切换成功", new LoginResult(access_token, byIdRel));
    }

    @Operation(method="getErrNum",description="获取登录错误次数")
    @GetMapping("/err_num")
    @NoLoginCheck
    public ApiResult<?> getErrNum(HttpServletRequest request,@RequestParam(value ="username")String username) {
        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"login",username);
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        return success("获取成功",_num);
    }

    @Operation(method="logout",description="用户退出登录")
    @GetMapping("/logout")
    @NoLoginCheck
    public ApiResult<?> logout(HttpServletRequest request) {
        this.deleted(request);
        return success("退出成功");
    }

    private void deleted(HttpServletRequest request){
        String accessToken = JwtUtil.getAccessToken(request);
        if(StringUtils.hasText(accessToken)){
            // 解析token
            try {
                Claims claims = JwtUtil.parseToken(accessToken, configProperties.getTokenKey());
                JwtSubject jwtSubject = JwtUtil.getJwtSubject(claims);
                String _tenantId =  jwtSubject.getTenantId().toString();
                String _userId = jwtSubject.getUserId().toString();
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getMessagePrefix(),_tenantId,_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getAnnouncementPrefix(),_tenantId,_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemMessagePrefix(),_userId));
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemAnnouncementPrefix(),_userId));
            }catch (Exception e){
                e.printStackTrace();
            }
            redisUtil.dels(JwtUtil.getAccessToken(request),true);
        }
    }

    @OperationLog
    @Operation(method="userInfo",description="获取登录用户信息(包含用户信息、角色信息、机构信息、菜单/权限信息)")
    @GetMapping("/auth/user")
    public ApiResult<User> userInfo(HttpServletRequest request,@RequestParam(value ="appType")String appType) {
        User loginUser = getLoginUser();
        String menuKey =CommonUtil.stringJoiner(configProperties.getMenuPrefix(),appType,JwtUtil.getAccessToken(request));
        Object menuJsons = redisUtil.get(menuKey);
        AuthoritieMenu authoritieMenu = null;
        if(menuJsons != null){
            authoritieMenu =  JSONUtil.parseObject(menuJsons.toString(),AuthoritieMenu.class);
        }else{
            List<Role> roles = loginUser.getRoles();
            if(!CollectionUtils.isEmpty(roles)){
                List<Integer> roleIds = new ArrayList<>(roles.size());
                for(Role role:roles){
                    roleIds.add(role.getRoleId());
                }
                List<Menu> menuList = roleMenuService.listMenuByRoleIds(roleIds, null, appType);
                authoritieMenu = new AuthoritieMenu(menuList);
                redisUtil.set(menuKey,JSONUtil.toJSONString(authoritieMenu),configProperties.getTokenExpireTime());
            }
        }
        loginUser.setAuthorities(authoritieMenu.getMenus());
        return success(loginUser);
    }

    @OperationLog
    @Operation(method="listMenu",description="查询用户菜单")
    @GetMapping("/auth/listMenu")
    public ApiResult<?> listMenu(@RequestParam(value = "menuType",required=false) Integer menuType,@RequestParam(value = "appType") String appType) {
        return  success(roleMenuService.listMenuByUserId(getLoginUserId(), menuType, appType));
    }

    @OperationLog
    @Operation(method="getTenant",description="获取用户的租户信息")
    @GetMapping("/auth/userTenant")
    public ApiResult<List<Tenant>> getTenant() {
        return success(userTenantService.listByUserId(getLoginUserId()));
    }

    @OperationLog
    @Operation(method="get",description="获取缓存的用户信息")
    @GetMapping("/auth/userInfo")
    public ApiResult<User> get() {
        return success(getLoginUser());
    }

    @OperationLog
    @AuthorityAnnotation("sys:auth:user")
    @Operation(method="updateInfo",description="修改个人信息")
    @PostMapping("/auth/user")
    public ApiResult<User> updateInfo(@RequestBody User user,HttpServletRequest request) {
        ValidatorUtil._validBean(user, User.UserUpdate.class);
        user.setUserId(getLoginUserId());
        if (StrUtil.isNotEmpty(user.getIdCard()) && userService.count(new LambdaQueryWrapper<User>().eq(User::getIdCard, user.getIdCard()).ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("身份证号已存在");
        }
        if (StrUtil.isNotEmpty(user.getPhone()) && userService.count(new LambdaQueryWrapper<User>().eq(User::getPhone, user.getPhone()).ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("手机号已存在");
        }
        if (StrUtil.isNotEmpty(user.getEmail()) && userService.count(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail()).ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("邮箱已存在");
        }
        if (userService.update(null, Wrappers.<User>lambdaUpdate()
                .set(User::getAvatar,user.getAvatar())
                .set(User::getRealname,user.getRealname())
                .set(User::getEmail,user.getEmail())
                .set(User::getSex,user.getSex())
                .set(User::getIdCard,user.getIdCard())
                .set(User::getPhone,user.getPhone())
                .set(User::getBirthday,user.getBirthday())
                .set(User::getPolitics,user.getPolitics())
                .set(User::getNation,user.getNation())
                .set(User::getOrganization,user.getOrganization())
                .set(User::getAddress,user.getAddress())
                .set(User::getIdPhoto,user.getIdPhoto())
                .set(User::getIdCard1,user.getIdCard1())
                .set(User::getIdCard2,user.getIdCard2())
                .set(User::getIntroduction,user.getIntroduction())
                .eq(User::getUserId,user.getUserId()))) {
            String key = CommonUtil.stringJoiner(configProperties.getUserPrefix(), JwtUtil.getAccessToken(request));
            Object o = redisUtil.get(key);
            User _user = new User();
            if(o != null){
                _user =  JSONUtil.parseObject(o.toString(),User.class);
                _user.setAvatar(user.getAvatar());
                _user.setBirthday(user.getBirthday());
                _user.setIdCard(user.getIdCard());
                _user.setPhone(user.getPhone());
                _user.setEmail(user.getEmail());
                _user.setSex(user.getSex());
                _user.setPolitics(user.getPolitics());
                _user.setNation(user.getNation());
                _user.setOrganization(user.getOrganization());
                _user.setAddress(user.getAddress());
                _user.setIntroduction(user.getIntroduction());
                _user.setIdPhoto(user.getIdPhoto());
                _user.setIdCard1(user.getIdCard1());
                _user.setIdCard2(user.getIdCard2());
                _user.setRealname(user.getRealname());
                redisUtil.set(key,JSONUtil.toJSONString(_user),configProperties.getTokenExpireTime());
            }
            return success(_user);
        }
        return fail("保存失败", null);
    }

    //有关键信息，不存日志
    @AuthorityAnnotation("sys:auth:password")
    @Operation(method="updatePassword",description="修改自己密码")
    @PostMapping("/auth/password")
    public ApiResult<?> updatePassword(@RequestBody UpdatePasswordParam param,HttpServletRequest request) {
        if (StrUtil.hasBlank(param.getOldPassword(), param.getPassword())) {
            return fail("参数不能为空");
        }
        Integer userId = getLoginUserId();
        if (userId == null) {
            return fail("未登录");
        }
        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"updatePassword",userId.toString());
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>3){
            return fail("原密码输入错误3次，请1小时后再重试");
        }
        if (!CommonUtil.comparePassword(userService.getById(userId).getPassword(), param.getOldPassword())) {
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("原密码输入不正确");
        }
        User user = new User();
        user.setUserId(userId);
        user.setPassword(CommonUtil.encodePassword(param.getPassword()));
        if (userService.updateById(user)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @Operation(method="captcha",description="图形验证码")
    @GetMapping("/captcha")
    @NoLoginCheck
    public ApiResult<CaptchaResult> captcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        String key = CommonUtil.stringJoiner(configProperties.getCodePrefix(), CommonUtil.randomUUID16());
        redisUtil.set(key,specCaptcha.text().toLowerCase(),configProperties.getCodeExpireTime());
        return success(new CaptchaResult(specCaptcha.toBase64(),key));
    }

    @Operation(method="send_captcha",description="发送手机号或者邮箱验证码")
    @PostMapping("/send_captcha")
    @NoLoginCheck
    public ApiResult<?> send_captcha(@RequestBody SendCaptchaParam param,HttpServletRequest request) {
        if(!StringUtils.hasText(param.getType())){
            return fail("找回密码类型不能为空");
        }
        String code = param.getCode();
        if(!StringUtils.hasText(code)){
            return fail("图形验证码不能为空", null);
        }
        String uuid = param.getUuid();
        if(!StringUtils.hasText(uuid)){
            return fail("图形验证码UUID不能为空", null);
        }
        Object _code = redisUtil.get(uuid);
        if(_code == null){
            return fail("图形验证码已过期", null);
        }else if(!_code.equals(code.toLowerCase())){
            return fail("图形验证码错误", null);
        }
        redisUtil.del(uuid);
        String key = configProperties.getCodePrefix();
        if(param.getType().equals("1")){
            if(!StringUtils.hasText(param.getEmail())){
                return fail("发送邮箱不能为空");
            }
            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, param.getEmail()));
            if(user == null){
                return fail("邮箱未找到绑定的用户");
            }
            key += param.getEmail();
        }
        if(param.getType().equals("2")){
            if(!StringUtils.hasText(param.getPhone())){
                return fail("发送手机号不能为空");
            }
            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, param.getPhone()));
            if(user == null){
                return fail("手机号未找到绑定的用户");
            }
            key += param.getPhone();
        }

        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"sendCaptcha",key);
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>10){
            return fail("已发送验证码过10次，1小时后再重试");
        }
        redisUtil.set(numKey,++_num+"",(long) 3600);

        //验证码
        String random = CommonUtil.random(6);
        redisUtil.set(key,random,configProperties.getCodeExpireTime());
        // TODO 发送验证码
        if(param.getType().equals("1")){
            emailRecordService.sendTextEmail("验证码", CommonUtil.stringJoiner("您的验证码是【",random,"】，此验证码用于修改密码，请不要泄露给他人，如果不是您本人操作，请忽略此邮件。"), param.getEmail().split(","));
        }

        return success("发送成功");
    }

    @Operation(method="modif_password",description="根据手机号或者邮箱修改密码")
    @PostMapping("/modif_password")
    @NoLoginCheck
    public ApiResult<?> modif_password(@RequestBody CaptchaUpdatePasswordParam param,HttpServletRequest request) {

        if(!StringUtils.hasText(param.getType())){
            return fail("找回密码类型不能为空");
        }
        String key = configProperties.getCodePrefix();
        if(param.getType().equals("1")){
            if(!StringUtils.hasText(param.getEmail())){
                return fail("邮箱不能为空");
            }
            key += param.getEmail();
        }else if(param.getType().equals("2")){
            if(!StringUtils.hasText(param.getPhone())){
                return fail("手机号不能为空");
            }
            key += param.getPhone();
        }
        String code = param.getCode();
        if(!StringUtils.hasText(code)){
            return fail("验证码不能为空", null);
        }

        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"modifPassword",key);
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>10){
            return fail("错误超过10次，1小时后再重试");
        }

        String _code = redisUtil.get(key);
        if(_code == null){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("验证码已过期", null);
        }else if(!_code.equals(code)){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("验证码错误", null);
        }
        redisUtil.del(key);
        User user = null;
        if(param.getType().equals("1")){
            user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, param.getEmail()));
            if(user == null){
                return fail("邮箱未找到绑定的用户");
            }
        }
        if(param.getType().equals("2")){
            user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, param.getPhone()));
            if(user == null){
                return fail("手机号未找到绑定的用户");
            }
        }
        User _user = new User();
        _user.setUserId(user.getUserId());
        _user.setPassword(CommonUtil.encodePassword(param.getPassword()));
        if (userService.updateById(_user)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @Operation(method="problem_password",description="根据安全问题修改密码")
    @PostMapping("/problem_password")
    @NoLoginCheck
    public ApiResult<?> problem_password(@RequestBody ProblemUpdatePasswordParam param,HttpServletRequest request) {
        if(!StringUtils.hasText(param.getUsername())){
            return fail("用户名不能为空");
        }
        List<String> answers = param.getAnswers();
        if(CollectionUtils.isEmpty(answers)){
            return fail("答案不能为空");
        }
        if(!StringUtils.hasText(param.getPassword())){
            return fail("新密码不能为空");
        }
        String numKey = CommonUtil.stringJoiner(ServletUtil.getClientIP(request),"problemPassword",param.getUsername());
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>10){
            return fail("错误10次，请1小时后再重试");
        }
        User  user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, param.getUsername()));
        if(user == null){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("未找到用户");
        }
        List<SysUserProblem> list = sysUserProblemService.list(new LambdaQueryWrapper<SysUserProblem>().eq(SysUserProblem::getUsername, param.getUsername()).orderByAsc(SysUserProblem::getSortNumber));
        if(CollectionUtils.isEmpty(list)){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("未设置安全问题");
        }
        if(answers.size() != list.size()){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("答案与安全问题数量不匹配");
        }
        boolean pass = true;
        for(int i=0;i<list.size();i++){
            SysUserProblem sysUserProblem = list.get(i);
            String answer = answers.get(i);
            if(!answer.equals(sysUserProblem.getAnswer())){
                pass = false;
                break;
            }
        }
        if(!pass){
            redisUtil.set(numKey,++_num+"",(long) 3600);
            return fail("输入的答案与安全问题答案不匹配");
        }
        User _user = new User();
        _user.setUserId(user.getUserId());
        _user.setPassword(CommonUtil.encodePassword(param.getPassword()));
        if (userService.updateById(_user)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }
}

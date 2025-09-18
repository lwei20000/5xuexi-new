package com.struggle.common.security.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.security.entity.SysUserProblem;
import com.struggle.common.security.service.SysUserProblemService;
import com.struggle.common.system.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author dxf
 * @ClassName: TSysUserProblemController
 * @Description: 安全问题-controller层
 */
@Tag(name = "忘记密码安全问题",description = "SysUserProblemController")
@RestController
@RequestMapping("/api/security/sysUserProblem")
public class SysUserProblemController extends BaseController {
    @Resource
    private SysUserProblemService sysUserProblemService;
    @Resource
    private RedisUtil redisUtil;

    //有关键信息，不存日志
    @AuthorityAnnotation("sys:auth:user")
    @Operation(method="saveOrUpdate",description="批量新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ApiResult<?> saveOrUpdate(@RequestBody List<SysUserProblem> list) {
        String s = ValidatorUtil.validBean(list);
        if(StringUtils.hasText(s)){
            return fail("参数错误："+s);
        }
        sysUserProblemService.saveOrUpdateProblem(list);
        return success("保存成功");
    }

    @OperationLog
    @AuthorityAnnotation("sys:auth:user")
    @Operation(method="unbinding",description= "解绑")
    @PostMapping(value = "/unbinding", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<?> unbinding() {
        sysUserProblemService.unbinding();
        return success("解绑成功");
    }

    @OperationLog
    @AuthorityAnnotation("sys:auth:user")
    @Operation(method="listByLoginName",description="获取安全问题集合")
    @GetMapping("/listByLoginName")
    public ApiResult<List<SysUserProblem>> listByLoginName() {
        User user = ThreadLocalContextHolder.getUser();
        LambdaQueryWrapper<SysUserProblem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserProblem::getUsername,user.getUsername());
        wrapper.select(SysUserProblem::getId,SysUserProblem::getProblem,SysUserProblem::getAnswer,SysUserProblem::getSortNumber);
        wrapper.orderByAsc(SysUserProblem::getSortNumber);
        return success(sysUserProblemService.list(wrapper));
    }

    @Operation(method="listByLoginName",description="根据loginName获取安全问题集合")
    @GetMapping("/not_listByLoginName")
    @NoLoginCheck
    public ApiResult<List<SysUserProblem>> listByLoginName(@RequestParam(value ="loginName")String loginName, HttpServletRequest request) {
        //防止恶意盗取信息
        String numKey = ServletUtil.getClientIP(request)+"GetUserProblem";
        String num = redisUtil.get(numKey);
        int _num = 0;
        if(num != null){
            _num = Integer.parseInt(num);
        }
        if(_num>10){
            throw new BusinessException("获取次数大于10次，请1小时后再重试");
        }
        redisUtil.set(numKey,++_num+"",(long) 3600);

        LambdaQueryWrapper<SysUserProblem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserProblem::getUsername,loginName);
        wrapper.select(SysUserProblem::getProblem,SysUserProblem::getSortNumber);
        wrapper.orderByAsc(SysUserProblem::getSortNumber);
        return success(sysUserProblemService.list(wrapper));
    }
}
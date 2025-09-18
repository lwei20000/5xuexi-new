package com.struggle.common.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.report.entity.OrgRemark;
import com.struggle.common.report.param.OrgRemarkParam;
import com.struggle.common.report.service.IOrgRemarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 * @ClassName: OrgRemarkController
 * @Description: 站点付费备注-controller层
 * @author xsk
 */
@Tag(name= "站点付费备注", description = "OrgRemarkController")
@RestController
@RequestMapping(value = "/api/report/orgRemark")
public class OrgRemarkController extends BaseController{

    @Resource
    private IOrgRemarkService orgRemarkService;

    @OperationLog
    @Operation(method="page",description="分页查询")
    @GetMapping(value = "/page")
    @AuthorityAnnotation("report:orgRemark:list:690f7899b44a89b06265b0d9")
    public ApiResult<PageResult<OrgRemark>> page(HttpServletRequest request, HttpServletResponse response,OrgRemarkParam param) {
        return success(orgRemarkService.pageRel(param));
    }

    @OperationLog
    @Operation(method="generate",description="生成当前年份数据")
    @PostMapping(value = "/generate")
    @AuthorityAnnotation("report:orgRemark:save:90f67899b449062995b60d9")
    public ApiResult<?> generate(String year,HttpServletRequest request,HttpServletResponse response) {
        orgRemarkService.generate(year);
        return success();
    }

    @OperationLog
    @Operation(method="save",description="新增")
    @PostMapping(value = "/save")
    @AuthorityAnnotation("report:orgRemark:save:90f67899b449062995b60d9")
    public ApiResult<?> save(@RequestBody OrgRemark orgRemark, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(orgRemark);
        if (orgRemarkService.count(new LambdaQueryWrapper<OrgRemark>()
                .eq(OrgRemark::getYear, orgRemark.getYear())
                .eq(OrgRemark::getOrgId, orgRemark.getOrgId())) > 0) {
            return fail("年份、机构唯一，已存在");
        }

        orgRemarkService.save(orgRemark);
        return success();
    }

    @OperationLog
    @Operation(method="update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("report:orgRemark:update:90f7869b44a90626695b60d9")
    public ApiResult<?> update(@RequestBody OrgRemark orgRemark, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(orgRemark);
        if (orgRemarkService.count(new LambdaQueryWrapper<OrgRemark>()
                .eq(OrgRemark::getYear, orgRemark.getYear())
                .eq(OrgRemark::getOrgId, orgRemark.getOrgId())
                .ne(OrgRemark::getId, orgRemark.getId())) > 0) {
            return fail("年份、机构唯一，已存在");
        }
        orgRemark.setUpdateTime(null);
        orgRemarkService.updateById(orgRemark);
        return success();
    }

    @OperationLog
    @Operation(method="delete",description= "删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("report:orgRemark:remove:69b494a86629995b0d6")
    public ApiResult<?> delete(HttpServletRequest request, HttpServletResponse response,@RequestBody List<Integer> idList) {
       return success(orgRemarkService.removeBatchByIds(idList));
    }
}
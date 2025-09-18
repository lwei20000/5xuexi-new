package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysStaticPicture;
import com.struggle.common.system.param.SysStaticPictureParam;
import com.struggle.common.system.service.ISysStaticPictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 *
 * @ClassName: SysStaticPictureController
 * @Description: 静态图片-controller层
 * @author xsk
 */
@Tag(name = "静态图片",description = "SysStaticPictureController")
@RestController
@RequestMapping("/api/system/sysStaticPicture")
public class SysStaticPictureController extends BaseController{
    @Resource
    private ISysStaticPictureService sysStaticPictureService;

    @OperationLog
    @Operation(method = "page",description="分页查询")
    @GetMapping("/page")
    @AuthorityAnnotation("system:sysStaticPicture:list:6160f78916b44a816b06265b0d16")
    public ApiResult<PageResult<SysStaticPicture>> page(SysStaticPictureParam param) {
        PageParam<SysStaticPicture, SysStaticPictureParam> page = new PageParam<>(param);
        return success(sysStaticPictureService.page(page, page.getWrapper()));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysStaticPicture:save:160f678169b44160629165b60d16")
    public ApiResult<?> save(@RequestBody SysStaticPicture sysStaticPicture) {
        ValidatorUtil._validBean(sysStaticPicture);
        if (sysStaticPictureService.count(new LambdaQueryWrapper<SysStaticPicture>().eq(SysStaticPicture::getUrl, sysStaticPicture.getUrl())) > 0) {
            throw new BusinessException("图片KEY已存在");
        }
        sysStaticPictureService.save(sysStaticPicture);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping("/update")
    @AuthorityAnnotation("system:sysStaticPicture:update:160f7869b44a160626695b60d16")
    public ApiResult<?> update(@RequestBody SysStaticPicture sysStaticPicture) {
        ValidatorUtil._validBean(sysStaticPicture);
        if (sysStaticPictureService.count(new LambdaQueryWrapper<SysStaticPicture>().eq(SysStaticPicture::getUrl, sysStaticPicture.getUrl()).ne(SysStaticPicture::getId,sysStaticPicture.getId())) > 0) {
            throw new BusinessException("图片KEY已存在");
        }
        sysStaticPictureService.updateById(sysStaticPicture);
        return success();
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysStaticPicture:remove:69b4164a8662169165b0d6")
    public ApiResult<?> delete(@RequestBody List<Integer> idList) {
       return success(sysStaticPictureService.removeBatchByIds(idList));
    }
}
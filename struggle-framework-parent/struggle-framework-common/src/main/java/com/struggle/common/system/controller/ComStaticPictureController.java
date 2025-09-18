package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.SysStaticPicture;
import com.struggle.common.system.service.ISysStaticPictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @ClassName: SysStaticPictureController
 * @Description: 静态图片-controller层
 * @author xsk
 */
@Tag(name = "静态图片",description = "ComStaticPictureController")
@RestController
@RequestMapping("/api/system/sysStaticPicture")
public class ComStaticPictureController extends BaseController{
    @Resource
    private ISysStaticPictureService sysStaticPictureService;

    @Operation(method="picture",description="静态图片地址")
    @GetMapping("/{key}")
    @NoLoginCheck
    public void picture(HttpServletResponse response,@PathVariable("key") String key) throws Exception {
        SysStaticPicture sysStaticPicture = sysStaticPictureService.getOne(new LambdaQueryWrapper<SysStaticPicture>().select(SysStaticPicture::getId,SysStaticPicture::getPicture).eq(SysStaticPicture::getUrl, key));
        if(sysStaticPicture!=null){
            response.sendRedirect(sysStaticPicture.getPicture());
        }
    }
}
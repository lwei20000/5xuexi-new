package com.struggle.common.oss.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.service.CoursewareService;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.oss.qiniu.service.QiniuService;
import com.struggle.common.oss.tianyiyun.service.TianyiyunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RestController
@Tag(name= "七牛云",description = "CallBackController")
@RequestMapping("/api/qiniu")
public class CallBackController extends BaseController {

    @Resource
    private CoursewareService coursewareService;

    @Autowired(required = false)
    private QiniuService qiniuService;

    @Autowired(required = false)
    private TianyiyunService tianyiyunService;

    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @GetMapping("/getToken")
    @Operation(method="getToken",description="获取token")
    public ApiResult<?> getToken() {
        return success(qiniuService.getToken());
    }

    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @GetMapping("/tianyiyun/getToken")
    @Operation(method="getTianyiyunToken",description="获取token")
    public ApiResult<?> getTianyiyunToken() {
        return success(tianyiyunService.getToken());
    }

    /**
     * js 上传七牛后保存
     *
     * @param dto
     * @return
     *
     */
    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @PostMapping("/callBack")
    @Operation(method="callBack",description="上传回调")
    public ApiResult<?> callBack(@RequestBody CallBackDTO dto) {
        if (dto.getCoursewareId() == null) {
            String[] fileNameStr = dto.getFileName().split("_");
            Integer parentId = 0;
            for(int i=1;i<fileNameStr.length;i++){
                String gropuFullName = fileNameStr[i].substring(2);
                String gropuSort = fileNameStr[i].substring(0,2);
                boolean f = false;
                if(i==(fileNameStr.length-1)){
                    f = true;
                }
                parentId = this.saveCourseware(parentId,gropuFullName,Integer.valueOf(gropuSort),f,dto);
            }
        } else {
            // 同步资源
            Courseware coursewareDb = coursewareService.getById(dto.getCoursewareId());
            if(coursewareDb != null){
                Courseware courseware = new Courseware();
                courseware.setCoursewareId(coursewareDb.getCoursewareId());
                courseware.setFileUrl(dto.getUrl());
                courseware.setDuration(dto.getFileDuration().intValue());
                courseware.setSize(dto.getFileSize().intValue());
                courseware.setFileType(dto.getSuffix());
                // 同步资源
                courseware.setUpdateTime(null);
                coursewareService.updateById(courseware);
            }
        }
        return success("ok");
    }

    private Integer saveCourseware(Integer parentId, String name, int sort, boolean flag, CallBackDTO dto){
        Courseware courseware = coursewareService.getOne(new LambdaQueryWrapper<Courseware>()
                .eq(Courseware::getCourseId, dto.getCourseId())
                .eq(Courseware::getParentId, parentId)
                .eq(Courseware::getCoursewareName,name));
        if(courseware == null){
            courseware = new Courseware();
            courseware.setCourseId(dto.getCourseId());
            courseware.setCoursewareName(name);
            courseware.setSortNumber(sort);
            courseware.setParentId(parentId);
            if(flag){
                courseware.setFileUrl(dto.getUrl());
                courseware.setDuration(dto.getFileDuration().intValue());
                courseware.setSize(dto.getFileSize().intValue());
                courseware.setFileType(dto.getSuffix());
            }
            coursewareService.save(courseware);
        }else{
            courseware.setUpdateTime(null);
            courseware.setSortNumber(sort);
            if(flag){
                courseware.setFileUrl(dto.getUrl());
                courseware.setDuration(dto.getFileDuration().intValue());
                courseware.setSize(dto.getFileSize().intValue());
                courseware.setFileType(dto.getSuffix());
            }
            coursewareService.updateById(courseware);
        }
        return courseware.getCoursewareId();
    }
}

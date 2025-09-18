package com.struggle.common.system.service.impl;

import com.struggle.common.system.entity.SysStaticPicture;
import com.struggle.common.system.service.ISysStaticPictureService;
import com.struggle.common.system.mapper.SysStaticPictureMapper;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 *
 * @ClassName: SysStaticPictureService
 * @Description:  静态图片-ServiceImpl层
 * @author xsk
 */
@Service
public class SysStaticPictureServiceImpl extends ServiceImpl<SysStaticPictureMapper, SysStaticPicture> implements  ISysStaticPictureService {

    @Resource
    private SysStaticPictureMapper sysStaticPictureMapper;

}
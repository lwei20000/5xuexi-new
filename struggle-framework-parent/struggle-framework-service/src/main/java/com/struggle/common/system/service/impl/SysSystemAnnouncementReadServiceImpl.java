package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncementRead;
import com.struggle.common.system.mapper.SysSystemAnnouncementReadMapper;
import com.struggle.common.system.param.SysSystemAnnouncementReadParam;
import com.struggle.common.system.service.ISysSystemAnnouncementReadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysSystemAnnouncementReadServiceImpl
 * @Description:  系统通知公告已读记录-ServiceImpl层
 * @author xsk
 */
@Service
public class SysSystemAnnouncementReadServiceImpl extends ServiceImpl<SysSystemAnnouncementReadMapper, SysSystemAnnouncementRead> implements ISysSystemAnnouncementReadService {

    @Resource
    private SysSystemAnnouncementReadMapper sysSystemAnnouncementReadMapper;

    @Override
    public PageResult<SysSystemAnnouncementRead> read_page(SysSystemAnnouncementReadParam param) {
        PageParam<SysSystemAnnouncementRead, SysSystemAnnouncementReadParam> page = new PageParam<>(param);
        List<SysSystemAnnouncementRead> sysSystemAnnouncementReads = sysSystemAnnouncementReadMapper.selectPageRel(page, param);
        return new PageResult<>(sysSystemAnnouncementReads, page.getTotal());
    }
}
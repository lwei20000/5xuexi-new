package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.entity.SysSystemAnnouncementReceive;
import com.struggle.common.system.mapper.SysSystemAnnouncementReceiveMapper;
import com.struggle.common.system.param.SysSystemAnnouncementReceiveParam;
import com.struggle.common.system.service.ISysSystemAnnouncementReceiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysSystemAnnouncementReceiveServiceImpl
 * @Description:  系统通知公告接收对象-ServiceImpl层
 * @author xsk
 */
@Service
public class SysSystemAnnouncementReceiveServiceImpl extends ServiceImpl<SysSystemAnnouncementReceiveMapper, SysSystemAnnouncementReceive> implements ISysSystemAnnouncementReceiveService {

    @Resource
    private SysSystemAnnouncementReceiveMapper sysSystemAnnouncementReceiveMapper;

    @Override
    public PageResult<SysSystemAnnouncement> pageRel(SysSystemAnnouncementReceiveParam param) {
        PageParam<SysSystemAnnouncement, SysSystemAnnouncementReceiveParam> page = new PageParam<>(param);
        List<SysSystemAnnouncement> list = sysSystemAnnouncementReceiveMapper.pageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public SysSystemAnnouncement get(SysSystemAnnouncementReceiveParam sysSystemAnnouncementReceiveParam) {
        return sysSystemAnnouncementReceiveMapper.get(sysSystemAnnouncementReceiveParam);
    }
}
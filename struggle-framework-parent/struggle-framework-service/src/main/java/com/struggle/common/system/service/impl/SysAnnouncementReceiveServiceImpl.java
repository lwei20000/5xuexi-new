package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysAnnouncementReceive;
import com.struggle.common.system.mapper.SysAnnouncementReceiveMapper;
import com.struggle.common.system.param.SysAnnouncementReceiveParam;
import com.struggle.common.system.service.ISysAnnouncementReceiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysAnnouncementReceiveService
 * @Description:  通知公告接收对象-ServiceImpl层
 * @author xsk
 */
@Service
public class SysAnnouncementReceiveServiceImpl extends ServiceImpl<SysAnnouncementReceiveMapper, SysAnnouncementReceive> implements  ISysAnnouncementReceiveService {

    @Resource
    private SysAnnouncementReceiveMapper sysAnnouncementReceiveMapper;

    @Override
    public PageResult<SysAnnouncement> pageRel(SysAnnouncementReceiveParam param) {
        PageParam<SysAnnouncement, SysAnnouncementReceiveParam> page = new PageParam<>(param);
        List<SysAnnouncement> list = sysAnnouncementReceiveMapper.pageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public SysAnnouncement get(SysAnnouncementReceiveParam sysAnnouncementReceiveParam) {
        return sysAnnouncementReceiveMapper.get(sysAnnouncementReceiveParam);
    }
}
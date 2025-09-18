package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysAnnouncementReceive;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.entity.SysMessageReceive;
import com.struggle.common.system.mapper.SysAnnouncementReceiveMapper;
import com.struggle.common.system.mapper.SysMessageReceiveMapper;
import com.struggle.common.system.param.SysAnnouncementReceiveParam;
import com.struggle.common.system.param.SysMessageReceiveParam;
import com.struggle.common.system.service.ISysAnnouncementReceiveService;
import com.struggle.common.system.service.ISysMessageReceiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysMessageReceiveService
 * @Description:  通知公告接收对象-ServiceImpl层
 * @author xsk
 */
@Service
public class SysMessageReceiveServiceImpl extends ServiceImpl<SysMessageReceiveMapper, SysMessageReceive> implements ISysMessageReceiveService {

    @Resource
    private SysMessageReceiveMapper sysMessageReceiveMapper;

    @Override
    public PageResult<SysMessage> pageRel(SysMessageReceiveParam param) {
        PageParam<SysMessage, SysMessageReceiveParam> page = new PageParam<>(param);
        List<SysMessage> list = sysMessageReceiveMapper.pageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public SysMessage get(SysMessageReceiveParam sysMessageReceiveParam) {
        return sysMessageReceiveMapper.get(sysMessageReceiveParam);
    }
}
package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncementRead;
import com.struggle.common.system.mapper.SysAnnouncementReadMapper;
import com.struggle.common.system.param.SysAnnouncementReadParam;
import com.struggle.common.system.service.ISysAnnouncementReadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysAnnouncementReadService
 * @Description:  通知公告已读记录-ServiceImpl层
 * @author xsk
 */
@Service
public class SysAnnouncementReadServiceImpl extends ServiceImpl<SysAnnouncementReadMapper, SysAnnouncementRead> implements  ISysAnnouncementReadService {

    @Resource
    private SysAnnouncementReadMapper sysAnnouncementReadMapper;

    @Override
    public PageResult<SysAnnouncementRead> read_page(SysAnnouncementReadParam param) {
        PageParam<SysAnnouncementRead, SysAnnouncementReadParam> page = new PageParam<>(param);
        List<SysAnnouncementRead> sysAnnouncementReads = sysAnnouncementReadMapper.selectPageRel(page, param);
        return new PageResult<>(sysAnnouncementReads, page.getTotal());
    }
}
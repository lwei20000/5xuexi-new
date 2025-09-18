package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.mapper.SysMessageReadMapper;
import com.struggle.common.system.param.SysMessageReadParam;
import com.struggle.common.system.service.ISysMessageReadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysMessageReadService
 * @Description:  通知公告已读记录-ServiceImpl层
 * @author xsk
 */
@Service
public class SysMessageReadServiceImpl extends ServiceImpl<SysMessageReadMapper, SysMessageRead> implements ISysMessageReadService {

    @Resource
    private SysMessageReadMapper sysMessageReadMapper;

    @Override
    public PageResult<SysMessageRead> read_page(SysMessageReadParam param) {
        PageParam<SysMessageRead, SysMessageReadParam> page = new PageParam<>(param);
        List<SysMessageRead> sysMessageReads = sysMessageReadMapper.selectPageRel(page, param);
        return new PageResult<>(sysMessageReads, page.getTotal());
    }
}
package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.entity.SysSystemMessageRead;
import com.struggle.common.system.mapper.SysMessageReadMapper;
import com.struggle.common.system.mapper.SysSystemMessageReadMapper;
import com.struggle.common.system.param.SysMessageReadParam;
import com.struggle.common.system.param.SysSystemMessageReadParam;
import com.struggle.common.system.service.ISysMessageReadService;
import com.struggle.common.system.service.ISysSystemMessageReadService;
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
public class SysSystemMessageReadServiceImpl extends ServiceImpl<SysSystemMessageReadMapper, SysSystemMessageRead> implements ISysSystemMessageReadService {

    @Resource
    private SysSystemMessageReadMapper sysSystemMessageReadMapper;

    @Override
    public PageResult<SysSystemMessageRead> read_page(SysSystemMessageReadParam param) {
        PageParam<SysSystemMessageRead, SysSystemMessageReadParam> page = new PageParam<>(param);
        List<SysSystemMessageRead> sysSystemMessageReads = sysSystemMessageReadMapper.selectPageRel(page, param);
        return new PageResult<>(sysSystemMessageReads, page.getTotal());
    }
}
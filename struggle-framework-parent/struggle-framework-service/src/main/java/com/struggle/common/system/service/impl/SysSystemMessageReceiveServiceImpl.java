package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.entity.SysSystemMessageReceive;
import com.struggle.common.system.mapper.SysSystemMessageReceiveMapper;
import com.struggle.common.system.param.SysSystemMessageReceiveParam;
import com.struggle.common.system.service.ISysSystemMessageReceiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysSystemMessageReceiveServiceImpl
 * @Description:  系统通知公告接收对象-ServiceImpl层
 * @author xsk
 */
@Service
public class SysSystemMessageReceiveServiceImpl extends ServiceImpl<SysSystemMessageReceiveMapper, SysSystemMessageReceive> implements ISysSystemMessageReceiveService {

    @Resource
    private SysSystemMessageReceiveMapper sysSystemMessageReceiveMapper;

    @Override
    public PageResult<SysSystemMessage> pageRel(SysSystemMessageReceiveParam param) {
        PageParam<SysSystemMessage, SysSystemMessageReceiveParam> page = new PageParam<>(param);
        List<SysSystemMessage> list = sysSystemMessageReceiveMapper.pageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public SysSystemMessage get(SysSystemMessageReceiveParam sysSystemMessageReceiveParam) {
        return sysSystemMessageReceiveMapper.get(sysSystemMessageReceiveParam);
    }
}
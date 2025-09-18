package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.entity.SysMessageReceive;
import com.struggle.common.system.param.SysMessageReceiveParam;

/**
 *
 * @ClassName: SysMessageReceiveService
 * @Description: 通知公告接收对象-Service层
 * @author xsk
 */
public interface ISysMessageReceiveService extends IService<SysMessageReceive>{

    PageResult<SysMessage> pageRel(SysMessageReceiveParam sysMessageReceiveParam);

    SysMessage get(SysMessageReceiveParam sysMessageReceiveParam);
}
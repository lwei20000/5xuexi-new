package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.entity.SysSystemMessageReceive;
import com.struggle.common.system.param.SysSystemMessageReceiveParam;

/**
 *
 * @ClassName: SysMessageReceiveService
 * @Description: 通知公告接收对象-Service层
 * @author xsk
 */
public interface ISysSystemMessageReceiveService extends IService<SysSystemMessageReceive>{

    PageResult<SysSystemMessage> pageRel(SysSystemMessageReceiveParam sysSystemMessageReceiveParam);

    SysSystemMessage get(SysSystemMessageReceiveParam sysSystemMessageReceiveParam);
}
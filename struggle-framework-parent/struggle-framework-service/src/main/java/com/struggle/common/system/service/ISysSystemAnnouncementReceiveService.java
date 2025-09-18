package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.entity.SysSystemAnnouncementReceive;
import com.struggle.common.system.param.SysSystemAnnouncementReceiveParam;

/**
 *
 * @ClassName: ISysSystemAnnouncementReceiveService
 * @Description: 系统通知公告接收对象-Service层
 * @author xsk
 */
public interface ISysSystemAnnouncementReceiveService extends IService<SysSystemAnnouncementReceive>{

    PageResult<SysSystemAnnouncement> pageRel(SysSystemAnnouncementReceiveParam sysSystemAnnouncementReceiveParam);

    SysSystemAnnouncement get(SysSystemAnnouncementReceiveParam sysSystemAnnouncementReceiveParam);
}
package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysAnnouncementReceive;
import com.struggle.common.system.param.SysAnnouncementReceiveParam;

/**
 *
 * @ClassName: SysAnnouncementReceiveService
 * @Description: 通知公告接收对象-Service层
 * @author xsk
 */
public interface ISysAnnouncementReceiveService extends IService<SysAnnouncementReceive>{

    PageResult<SysAnnouncement> pageRel(SysAnnouncementReceiveParam sysAnnouncementReceiveParam);

    SysAnnouncement get(SysAnnouncementReceiveParam sysAnnouncementReceiveParam);
}
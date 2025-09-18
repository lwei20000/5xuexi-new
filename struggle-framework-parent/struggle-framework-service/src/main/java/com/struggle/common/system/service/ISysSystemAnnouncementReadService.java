package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncementRead;
import com.struggle.common.system.param.SysSystemAnnouncementReadParam;

/**
 *
 * @ClassName: ISysSystemAnnouncementReadService
 * @Description: 系统通知公告已读记录-Service层
 * @author xsk
 */
public interface ISysSystemAnnouncementReadService extends IService<SysSystemAnnouncementRead>{

    PageResult<SysSystemAnnouncementRead> read_page(SysSystemAnnouncementReadParam param);
}
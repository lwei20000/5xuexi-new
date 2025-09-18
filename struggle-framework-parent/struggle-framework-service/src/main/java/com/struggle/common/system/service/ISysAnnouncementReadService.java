package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncementRead;
import com.struggle.common.system.param.SysAnnouncementReadParam;

/**
 *
 * @ClassName: SysAnnouncementReadService
 * @Description: 通知公告已读记录-Service层
 * @author xsk
 */
public interface ISysAnnouncementReadService extends IService<SysAnnouncementRead>{

    PageResult<SysAnnouncementRead> read_page(SysAnnouncementReadParam param);
}
package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.param.SysSystemAnnouncementParam;

import java.util.List;

/**
 *
 * @ClassName: ISysSystemAnnouncementService
 * @Description: 系统通知公告-Service层
 * @author xsk
 */
public interface ISysSystemAnnouncementService extends IService<SysSystemAnnouncement>{

    PageResult<SysSystemAnnouncement>  pageRel(SysSystemAnnouncementParam param);

    SysSystemAnnouncement getDetailById(Integer id);

    void saveSystemAnnouncement(SysSystemAnnouncement sysSystemAnnouncement);

    void updateSystemAnnouncement(SysSystemAnnouncement sysSystemAnnouncement);

    void deleteSystemAnnouncement( List<Integer> idList);

}
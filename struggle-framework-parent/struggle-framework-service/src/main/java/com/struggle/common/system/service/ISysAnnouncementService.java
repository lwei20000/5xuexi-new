package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.param.SysAnnouncementParam;

import java.util.List;

/**
 *
 * @ClassName: SysAnnouncementService
 * @Description: 通知公告-Service层
 * @author xsk
 */
public interface ISysAnnouncementService extends IService<SysAnnouncement>{

    PageResult<SysAnnouncement>  pageRel(SysAnnouncementParam param);

    SysAnnouncement getDetailById(Integer id);

    void saveAnnouncement(SysAnnouncement sysAnnouncement);

    void updateAnnouncement(SysAnnouncement sysAnnouncement);

    void deleteAnnouncement( List<Integer> idList);

}
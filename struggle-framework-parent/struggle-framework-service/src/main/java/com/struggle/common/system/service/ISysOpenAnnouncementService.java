package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysOpenAnnouncement;
import com.struggle.common.system.param.SysOpenAnnouncementParam;

import java.util.List;

/**
 *
 * @ClassName: SysOpenAnnouncementService
 * @Description: 通知公告-Service层
 * @author xsk
 */
public interface ISysOpenAnnouncementService extends IService<SysOpenAnnouncement>{

    PageResult<SysOpenAnnouncement>  pageRel(SysOpenAnnouncementParam param);

    SysOpenAnnouncement getDetailById(Integer id);

    void saveAnnouncement(SysOpenAnnouncement sysAnnouncement);

    void updateAnnouncement(SysOpenAnnouncement sysAnnouncement);

    void deleteAnnouncement( List<Integer> idList);

}
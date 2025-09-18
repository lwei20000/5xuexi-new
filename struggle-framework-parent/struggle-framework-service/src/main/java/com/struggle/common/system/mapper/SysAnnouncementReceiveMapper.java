package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysAnnouncementReceive;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.SysAnnouncementReceiveParam;
import com.struggle.common.system.param.UserParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: SysAnnouncementReceive
 * @Description: 通知公告接收对象-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysAnnouncementReceiveMapper extends BaseMapper<SysAnnouncementReceive> {

    List<SysAnnouncement> pageRel(@Param("page") IPage<SysAnnouncement> page,
                                  @Param("param") SysAnnouncementReceiveParam param);

    SysAnnouncement get(@Param("param")  SysAnnouncementReceiveParam sysAnnouncementReceiveParam);
}
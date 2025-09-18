package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.entity.SysSystemAnnouncementReceive;
import com.struggle.common.system.param.SysSystemAnnouncementReceiveParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: SysSystemAnnouncementReceiveMapper
 * @Description: 系统通知公告接收对象-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysSystemAnnouncementReceiveMapper extends BaseMapper<SysSystemAnnouncementReceive> {

    @InterceptorIgnore(tenantLine = "true")
    List<SysSystemAnnouncement> pageRel(@Param("page") IPage<SysSystemAnnouncement> page,@Param("param") SysSystemAnnouncementReceiveParam param);

    @InterceptorIgnore(tenantLine = "true")
    SysSystemAnnouncement get(@Param("param")  SysSystemAnnouncementReceiveParam sysSystemAnnouncementReceiveParam);
}
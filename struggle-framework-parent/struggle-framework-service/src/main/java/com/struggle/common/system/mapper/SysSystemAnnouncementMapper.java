package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.SysSystemAnnouncement;

/**
 *
 * @ClassName: SysSystemAnnouncementMapper
 * @Description: 系统通知公告-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysSystemAnnouncementMapper extends BaseMapper<SysSystemAnnouncement> {

}
package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.entity.SysAnnouncementRead;
import com.struggle.common.system.param.DictionaryParam;
import com.struggle.common.system.param.SysAnnouncementReadParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysAnnouncementRead
 * @Description: 通知公告已读记录-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysAnnouncementReadMapper extends BaseMapper<SysAnnouncementRead> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Dictionary>
     */
    List<SysAnnouncementRead> selectPageRel(@Param("page") IPage<SysAnnouncementRead> page,
                                   @Param("param") SysAnnouncementReadParam param);

    List<Map<String,Object>> _selectCount(@Param("param") SysAnnouncementReadParam param);
}
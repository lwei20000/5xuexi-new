package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.param.SysMessageReadParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysMessageRead
 * @Description: 通知公告已读记录-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysMessageReadMapper extends BaseMapper<SysMessageRead> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Dictionary>
     */
    List<SysMessageRead> selectPageRel(@Param("page") IPage<SysMessageRead> page,
                                   @Param("param") SysMessageReadParam param);

    List<Map<String,Object>> _selectCount(@Param("param") SysMessageReadParam param);
}
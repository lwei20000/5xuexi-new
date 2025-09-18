package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.param.GroupParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织分组Mapper
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@DS("system")
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Group>
     */
    List<Group> selectPageRel(@Param("page") IPage<Group> page, @Param("param") GroupParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<Group>
     */
    List<Group> selectListRel(@Param("param") GroupParam param,@Param("tenantIds") List<Integer> tenantIds,@Param("tenantId") Integer tenantId);

}

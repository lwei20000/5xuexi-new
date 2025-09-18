package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.param.OrgParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织机构Mapper
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@DS("system")
public interface OrgMapper extends BaseMapper<Org> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Org>
     */
    List<Org> selectPageRel(@Param("page") IPage<Org> page, @Param("param") OrgParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<Org>
     */
    List<Org> selectListRel(@Param("param") OrgParam param,@Param("tenantIds") List<Integer> tenantIds,@Param("tenantId") Integer tenantId);

}

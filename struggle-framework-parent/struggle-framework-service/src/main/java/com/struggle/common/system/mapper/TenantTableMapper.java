package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.TenantTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TenantTableMapper
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */

@DS("system")
public interface TenantTableMapper extends BaseMapper<TenantTable> {

    /**o
     * 查询所有租户创建表
     * @return
     */
    List<TenantTable> _selectList();

    /**
     * 执行sql
     * @param dbCode
     * @param sql
     */
    @DS("#dbCode")
    void execute(String dbCode,@Param("_sql") String sql);
}

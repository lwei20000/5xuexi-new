package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.param.DictionaryDataParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@DS("system")
public interface DictionaryDataMapper extends BaseMapper<DictionaryData> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<DictionaryData>
     */
    List<DictionaryData> selectPageRel(@Param("page") IPage<DictionaryData> page,
                                       @Param("param") DictionaryDataParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<DictionaryData>
     */
    List<DictionaryData> selectListRel(@Param("param") DictionaryDataParam param);

}

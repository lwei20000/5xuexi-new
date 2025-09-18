package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.param.DictionaryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典Mapper
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:03
 */
@DS("system")
public interface DictionaryMapper extends BaseMapper<Dictionary> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Dictionary>
     */
    List<Dictionary> selectPageRel(@Param("page") IPage<Dictionary> page,
                                       @Param("param") DictionaryParam param);
}

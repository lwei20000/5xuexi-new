package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.UserFile;
import com.struggle.common.system.param.UserFileParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户文件Mapper
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
@DS("system")
public interface UserFileMapper extends BaseMapper<UserFile> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<FileRecord>
     */
    List<UserFile> selectPageRel(@Param("page") IPage<UserFile> page,
                                   @Param("param") UserFileParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<FileRecord>
     */
    List<UserFile> selectListRel(@Param("param") UserFileParam param);
}

package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.UserMajor;
import com.struggle.common.base.param.UserMajorParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学生与专业关系Mapper
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface UserMajorMapper extends BaseMapper<UserMajor> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<StudentMajor>
     */
    List<UserMajor> selectPageRel(@Param("page") IPage<UserMajor> page,
                                  @Param("param") UserMajorParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<UserMajor> selectListRel(@Param("param") UserMajorParam param);

    /**
     * 获取站点人数
     * @param param
     * @return
     */
    List<Map> selectCountRel(@Param("param") UserMajorParam param);
}

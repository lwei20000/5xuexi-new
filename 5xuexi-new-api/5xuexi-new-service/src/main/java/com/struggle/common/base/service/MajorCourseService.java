package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.CopyMajorCourseParam;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 专业与课程关系Service
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface MajorCourseService extends IService<MajorCourse> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<MajorCourse>
     */
    PageResult<MajorCourse> pageRel(MajorCourseParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<MajorCourse>
     */
    List<MajorCourse> listRel(MajorCourseParam param);

    /**
     * 新增
     * @param majorCourse
     * @return
     */
    boolean saveMajorCourse(MajorCourse majorCourse);

    boolean updateMajorCourse(MajorCourse majorCourse);
    /**
     * 根据id查询
     *
     * @param id 专业与课程关系id
     * @return MajorCourse
     */
    MajorCourse getByIdRel(Integer id);

    /**
     * 复制专业
     *
     * @return boolean
     */
    boolean copy(CopyMajorCourseParam majorCourse);

    /**
     * 导入
     * @param file
     * @return
     */
    ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res);

    /**
     * 导出Excel模板
     */
    void templateExport(HttpServletResponse response);
}

package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserCourseService extends IService<UserCourse> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<StudentMajor>
     */
    PageResult<UserCourse> pageRel(UserCourseParam param);

    List<UserCourse> listRel(UserCourseParam param,boolean detail);
    /**
     * 导出Excel数据
     */
    void dataExport(HttpServletResponse response,UserCourseParam param);

    void updateScore(UserCourseParam param);

    void updateExamScore(UserCourse param,List<Integer> orgIds);
    /**
     * 导入
     * @param file
     * @return
     */
    ApiResult<?> scoreImportBatch(MultipartFile file,UserCourseParam param, HttpServletRequest request, HttpServletResponse res);

    /**
     * 导出Excel模板
     */
    void scoreTemplateExport(HttpServletResponse response);
}

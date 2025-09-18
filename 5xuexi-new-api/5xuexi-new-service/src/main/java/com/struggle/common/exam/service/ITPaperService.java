package com.struggle.common.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TPaper;
import com.struggle.common.exam.param.TPaperParam;

/**
 *
 * @ClassName: TPaperService
 * @Description: 试卷-Service层
 * @author xsk
 */
public interface ITPaperService extends IService<TPaper>{

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<TPaper>
     */
    PageResult<TPaper> pageCourseRel(TPaperParam param);
}
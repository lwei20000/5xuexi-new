package com.struggle.common.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.exam.entity.TPaperQuestion;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @ClassName: TPaperQuestionService
 * @Description: 课程试卷题目-Service层
 * @author xsk
 */
public interface ITPaperQuestionService extends IService<TPaperQuestion>{


    /**
     * 导入
     * @param file
     * @return
     */
    ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res,Integer paperId);

    /**
     * 导出Excel模板
     */
    void templateExport(HttpServletResponse response);
}
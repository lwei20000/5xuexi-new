package com.struggle.common.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.exam.entity.TUserMajorCourseExamItem;
import com.struggle.common.exam.mapper.TUserMajorCourseExamItemMapper;
import com.struggle.common.exam.service.ITUserMajorCourseExamItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
/**
 *
 * @ClassName: TUserMajorCourseExamItemService
 * @Description:  用户专业考试记录详情-ServiceImpl层
 * @author xsk
 */
@Service
public class TUserMajorCourseExamItemServiceImpl extends ServiceImpl<TUserMajorCourseExamItemMapper, TUserMajorCourseExamItem> implements  ITUserMajorCourseExamItemService {

    @Resource
    private TUserMajorCourseExamItemMapper tUserMajorCourseExamItemMapper;

}
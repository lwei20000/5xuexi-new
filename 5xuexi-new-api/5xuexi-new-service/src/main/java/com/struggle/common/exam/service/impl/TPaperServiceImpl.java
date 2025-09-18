package com.struggle.common.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.service.CourseService;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TPaper;
import com.struggle.common.exam.mapper.TPaperMapper;
import com.struggle.common.exam.param.TPaperParam;
import com.struggle.common.exam.service.ITPaperService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: TPaperService
 * @Description:  试卷-ServiceImpl层
 * @author xsk
 */
@Service
public class TPaperServiceImpl extends ServiceImpl<TPaperMapper, TPaper> implements  ITPaperService {

    @Resource
    private CourseService courseService;

    @Override
    public PageResult<TPaper> pageCourseRel(TPaperParam param) {
        PageParam<TPaper, TPaperParam> page = new PageParam<>(param);
        //page.setDefaultOrder("majorYear desc");
        List<TPaper> list = baseMapper.pageCourseRel(page, param);
        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    private void initOther(List<TPaper> list){
        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> courseIds = new ArrayList<>();
            for (TPaper paper : list) {
                courseIds.add(paper.getCourseId());
            }
            CourseParam c = new CourseParam();
            c.setCourseIds(new ArrayList<>(courseIds));
            List<Course> courses = courseService.listRel(c);
            Map<Integer, Course> courseMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(courses)) {
                courseMap = courses.stream().collect(Collectors.toMap(Course::getCourseId, Course -> Course));
            }

            Map<Integer, TPaper> paperMap = new HashMap<>();
            List<TPaper> tPapers = baseMapper.selectList(new LambdaQueryWrapper<TPaper>().in(TPaper::getCourseId, courseIds).eq(TPaper::getPaperUsage, 1));
            if (!CollectionUtils.isEmpty(tPapers)) {
                paperMap = tPapers.stream().collect(Collectors.toMap(TPaper::getCourseId, TPaper -> TPaper));
            }

            for (TPaper paper : list) {
                TPaper tPaper = paperMap.get(paper.getCourseId());
                if(tPaper != null){
                    BeanUtils.copyProperties(tPaper,paper);
                }
                paper.setCourse(courseMap.get(paper.getCourseId()));
            }
        }
    }
}
package com.struggle.common.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.base.service.CourseService;
import com.struggle.common.base.service.MajorService;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.entity.TPaper;
import com.struggle.common.exam.mapper.TMajorCourseExamMapper;
import com.struggle.common.exam.mapper.TPaperMapper;
import com.struggle.common.exam.param.TMajorCourseExamParam;
import com.struggle.common.exam.service.ITMajorCourseExamService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: TMajorCourseExamService
 * @Description:  专业课程考试-ServiceImpl层
 * @author xsk
 */
@Service
public class TMajorCourseExamServiceImpl extends ServiceImpl<TMajorCourseExamMapper, TMajorCourseExam> implements  ITMajorCourseExamService {

    @Resource
    private MajorService majorService;

    @Resource
    private CourseService courseService;
    
    @Resource
    private TPaperMapper paperMapper;


    @Override
    public PageResult<MajorCourse> pageRel(MajorCourseParam param) {
        PageParam<MajorCourse, MajorCourseParam> page = new PageParam<MajorCourse, MajorCourseParam>(param);
        page.setDefaultOrder("b.major_year desc,b.major_id,a.semester desc"); //desc只能小写
        List<MajorCourse> list = baseMapper.selectPageRel(page, param);
        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PageResult<TMajorCourseExam> pageRel(TMajorCourseExamParam param) {
        PageParam<TMajorCourseExam, TMajorCourseExamParam> page = new PageParam<>(param);
        page.setDefaultOrder("end_time desc");
        PageParam<TMajorCourseExam, TMajorCourseExamParam> majorCourseExams = this.page(page, page.getWrapper());
        this.initOtherExam(majorCourseExams.getRecords());
        return new PageResult<>(majorCourseExams.getRecords(),majorCourseExams.getTotal());
    }

    @Override
    public void batch_save(MajorCourseParam param) {

        if(param.getStartTime() == null){
            throw new BusinessException("考试开始时间不能为空");
        }

        if(param.getEndTime() == null){
            throw new BusinessException("考试结束时间不能为空");
        }

        List<MajorCourse> majorCourses = baseMapper.selectListRel(param);
        if(!CollectionUtils.isEmpty(majorCourses)){
            Set<Integer> courseIds = new HashSet<>();
            Set<Integer> majorCourseIds = new HashSet<>();
            for(MajorCourse majorCourse:majorCourses){
                majorCourseIds.add(majorCourse.getId());
                courseIds.add(majorCourse.getCourseId());
            }

            List<TPaper> tPapers = paperMapper.selectList(new LambdaQueryWrapper<TPaper>().eq(TPaper::getPaperUsage,1).in(TPaper::getCourseId, courseIds));
            if(!CollectionUtils.isEmpty(tPapers)){
                Map<Integer,TPaper> tPaperMap = tPapers.stream().collect(Collectors.toMap(TPaper::getCourseId,item -> item));
                //已经安排考试的
                List<TMajorCourseExam> tMajorCourseExams = baseMapper.selectList(new LambdaQueryWrapper<TMajorCourseExam>()
                        .in(TMajorCourseExam::getMajorCourseId, majorCourseIds)
                        .gt(TMajorCourseExam::getEndTime, new Date()));

                Map<Integer,List<TMajorCourseExam>> tMajorCourseExamMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(tMajorCourseExams)){
                    //只要有一个比现在的时间大的就可以
                    tMajorCourseExamMap =  tMajorCourseExams.stream().collect(Collectors.groupingBy(TMajorCourseExam::getMajorCourseId));
                }

                List<TMajorCourseExam> majorCourseExamList = new ArrayList<>();
                for(MajorCourse majorCourse:majorCourses){
                    TPaper tPaper = tPaperMap.get(majorCourse.getCourseId());
                    if(tMajorCourseExamMap.get(majorCourse.getId()) == null && tPaper != null){
                        TMajorCourseExam majorCourseExam = new TMajorCourseExam();
                        majorCourseExam.setMajorCourseId(majorCourse.getId());
                        majorCourseExam.setPaperId(tPaper.getPaperId());
                        majorCourseExam.setStartTime(param.getStartTime());
                        majorCourseExam.setEndTime(param.getEndTime());
                        majorCourseExamList.add(majorCourseExam);
                    }
                }

                if(!CollectionUtils.isEmpty(majorCourseExamList)){
                    this.saveBatch(majorCourseExamList,3000);
                }
            }
        }
    }

    @Override
    public int selectTotal(TMajorCourseExam tMajorCourseExam) {

        return baseMapper.selectTotal(tMajorCourseExam);
    }

    private void initOtherExam(List<TMajorCourseExam> majorCourseExams) {
        if (!CollectionUtils.isEmpty(majorCourseExams)) {
            Set<Integer> paperIds = new HashSet<>();
            for (TMajorCourseExam majorCourseExam : majorCourseExams) {
                paperIds.add(majorCourseExam.getPaperId());
            }

            List<TPaper> tPapers = paperMapper.selectBatchIds(paperIds);
            Map<Integer, TPaper> paperMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(tPapers)) {
                paperMap = tPapers.stream().collect(Collectors.toMap(TPaper::getPaperId, Paper -> Paper));
            }
            for (TMajorCourseExam majorCourseExam : majorCourseExams) {
                majorCourseExam.setPaper(paperMap.get(majorCourseExam.getPaperId()));
            }
        }
    }

    private void initOther(List<MajorCourse> majorCourses){
        if(!CollectionUtils.isEmpty(majorCourses)){
            Set<Integer> courseIds = new HashSet<>();
            Set<Integer> majorIds = new HashSet<>();
            Set<Integer> majorCourseIds = new HashSet<>();
            for (MajorCourse majorCourse : majorCourses) {
                courseIds.add(majorCourse.getCourseId());
                majorIds.add(majorCourse.getMajorId());
                majorCourseIds.add(majorCourse.getId());
            }
            List<Major> majors = majorService.listByIds(majorIds);
            Map<Integer, Major> majorMap= new HashMap<>();
            if(!CollectionUtils.isEmpty(majors)){
                majorMap = majors.stream().collect(Collectors.toMap(Major::getMajorId, Major -> Major));
            }

            CourseParam c = new CourseParam();
            c.setCourseIds(new ArrayList<>(courseIds));
            List<Course> courses = courseService.listRel(c);
            Map<Integer, Course> courseMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(courses)) {
                courseMap = courses.stream().collect(Collectors.toMap(Course::getCourseId, Course -> Course));
            }

            List<TMajorCourseExam> tMajorCourseExams = baseMapper.selectList(new LambdaQueryWrapper<TMajorCourseExam>()
                    .in(TMajorCourseExam::getMajorCourseId, majorCourseIds)
                    .orderByDesc(TMajorCourseExam::getEndTime));

            Map<Integer,TMajorCourseExam> majorCourseExamMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(tMajorCourseExams)){
                Set<Integer> paperIds = new HashSet<>();
                for(TMajorCourseExam majorCourseExam:tMajorCourseExams){
                    paperIds.add(majorCourseExam.getPaperId());
                }

                List<TPaper> tPapers = paperMapper.selectBatchIds(paperIds);
                Map<Integer,TPaper> paperMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(tPapers)){
                    paperMap = tPapers.stream().collect(Collectors.toMap(TPaper::getPaperId, Paper -> Paper));
                }
                for(TMajorCourseExam majorCourseExam:tMajorCourseExams){
                    TMajorCourseExam tMajorCourseExam = majorCourseExamMap.get(majorCourseExam.getMajorCourseId());
                    if(tMajorCourseExam == null){
                        majorCourseExam.setPaper(paperMap.get(majorCourseExam.getPaperId()));
                        majorCourseExamMap.put(majorCourseExam.getMajorCourseId(),majorCourseExam);
                    }else{
                        if(majorCourseExam.getEndTime().after(new Date())){
                            majorCourseExam.setPaper(paperMap.get(majorCourseExam.getPaperId()));
                            majorCourseExamMap.put(majorCourseExam.getMajorCourseId(),majorCourseExam);
                        }
                    }
                }
            }

            for (MajorCourse majorCourse : majorCourses) {
                majorCourse.setMajor(majorMap.get(majorCourse.getMajorId()));
                majorCourse.setCourse(courseMap.get(majorCourse.getCourseId()));
                majorCourse.setMajorCourseExam(majorCourseExamMap.get(majorCourse.getId()));
            }
        }
    }
}
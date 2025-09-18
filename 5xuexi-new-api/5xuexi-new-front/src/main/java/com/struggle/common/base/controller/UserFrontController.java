package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.base.entity.*;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.base.service.*;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.core.world.WordOperation;
import com.struggle.common.exam.entity.*;
import com.struggle.common.exam.service.*;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.OrgService;
import com.struggle.common.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "用户的接口",description = "UserFrontController")
@RestController
@RequestMapping("/api/front")
public class UserFrontController extends BaseController {
    @Resource
    private UserMajorService userMajorService;
    @Resource
    private MajorService majorService;
    @Resource
    private MajorCourseService majorCourseService;
    @Resource
    private CourseService courseService;
    @Resource
    private CoursewareService coursewareService;
    @Resource
    private UserLearningService userLearningService;
    @Resource
    private UserCourseService userCourseService;
    @Resource
    private ITMajorCourseExamService tMajorCourseExamService;
    @Resource
    private ITUserMajorCourseExamService tUserMajorCourseExamService;
    @Resource
    private ITUserMajorCourseExamItemService tUserMajorCourseExamItemService;
    @Resource
    private ITPaperService tPaperService;
    @Resource
    private ITPaperQuestionService tPaperQuestionService;
    @Resource
    private ScoreRuleService scoreRuleService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserService userService;
    @Autowired
    private OrgService orgService;

    @OperationLog
    @Operation(method="majorList",description="查询用户的专业")
    @GetMapping(value = "/studentMajor")
    public ApiResult<List<FrontUserMajor>> majorList() {
        Integer loginUserId = getLoginUserId();
        List<FrontUserMajor> list = new ArrayList<>();
        //用户的专业
        List<UserMajor> userMajorList = userMajorService.list(new LambdaQueryWrapper<UserMajor>().eq(UserMajor::getUserId, loginUserId));
        if(!CollectionUtils.isEmpty(userMajorList)){
            //专业IDS 集合
            List<Integer> majorIds = userMajorList.stream().map(UserMajor::getMajorId).collect(Collectors.toList());

            //成绩比例
            List<ScoreRule> scoreRuleList = scoreRuleService.list();

            //专业信息
            List<Major>  majorList = majorService.list(new LambdaQueryWrapper<Major>().in(Major::getMajorId, majorIds));
            Map<Integer,Major> majorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(majorList)){
                for(Major major:majorList){
                    majorMap.put(major.getMajorId(),major);
                }
            }
            //专业课程
            List<MajorCourse> majorCourseList = majorCourseService.list(new LambdaQueryWrapper<MajorCourse>().in(MajorCourse::getMajorId, majorIds));
            Map<Integer,List<MajorCourse>> majorCourseMap = new HashMap<>();
            Map<Integer,Course> courseMap = new HashMap<>();
            Map<String,UserCourse> userCourseMap = new HashMap<>();
            Map<Integer,TMajorCourseExam> majorCourseExamMap = new HashMap<>();
            Map<Integer,List<TMajorCourseExam>> majorCourseExamAllMap = new HashMap<>();
            Map<Integer,TUserMajorCourseExam> userMajorCourseExamMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(majorCourseList)){
                Set<Integer> courseIds = new HashSet<>();
                List<Integer> majorCourseIds = new ArrayList<>();
                for(MajorCourse majorCourse:majorCourseList){
                    majorCourseIds.add(majorCourse.getId());
                    courseIds.add(majorCourse.getCourseId());
                    List<MajorCourse> majorCourses = majorCourseMap.get(majorCourse.getMajorId());
                    if(majorCourses != null){
                        majorCourses.add(majorCourse);
                    }else{
                        majorCourses = new ArrayList<>();
                        majorCourses.add(majorCourse);
                        majorCourseMap.put(majorCourse.getMajorId(),majorCourses);
                    }
                }

                //课程信息
                CourseParam param = new CourseParam();
                param.setCourseIds(new ArrayList<>(courseIds));
                List<Course> courseList = courseService.listRel(param);
                if(!CollectionUtils.isEmpty(courseList)){
                    for(Course course:courseList){
                        course.setUpdateTime(null);
                        course.setCreateTime(null);
                        course.setComments(null);
                        course.setDeleted(null);
                        courseMap.put(course.getCourseId(),course);
                    }
                }

                //用户课程
                List<UserCourse> list1 = userCourseService.list(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId, loginUserId));
                if(!CollectionUtils.isEmpty(list1)){
                    for(UserCourse userCourse:list1){
                        userCourseMap.put(userCourse.getMajorId()+"_"+userCourse.getCourseId(),userCourse);
                    }
                }
                //专业课程考试
                List<TMajorCourseExam> tMajorCourseExams = tMajorCourseExamService.list(new LambdaQueryWrapper<TMajorCourseExam>()
                        .in(TMajorCourseExam::getMajorCourseId, majorCourseIds)
                        .orderByDesc(TMajorCourseExam::getEndTime));
                List<Integer> majorCourseExamIds = new ArrayList<>();
                if(!CollectionUtils.isEmpty(tMajorCourseExams)){
                    Date nowDate = new Date();
                    for(TMajorCourseExam majorCourseExam:tMajorCourseExams){
                        majorCourseExamIds.add(majorCourseExam.getMajorCourseExamId());
                        if(majorCourseExam.getEndTime().after(nowDate)){
                            majorCourseExamMap.put(majorCourseExam.getMajorCourseId(),majorCourseExam);
                        }

                        List<TMajorCourseExam> tMajorCourseExams1 = majorCourseExamAllMap.get(majorCourseExam.getMajorCourseId());
                        if(!CollectionUtils.isEmpty(tMajorCourseExams1)){
                            tMajorCourseExams1.add(majorCourseExam);
                        }else{
                            tMajorCourseExams1 = new ArrayList<>();
                            tMajorCourseExams1.add(majorCourseExam);
                            majorCourseExamAllMap.put(majorCourseExam.getMajorCourseId(),tMajorCourseExams1);
                        }
                    }
                }
                //用户考试记录
                if(!CollectionUtils.isEmpty(majorCourseExamIds)){
                    List<TUserMajorCourseExam> userMajorCourseExamList = tUserMajorCourseExamService.list(new LambdaQueryWrapper<TUserMajorCourseExam>()
                             .eq(TUserMajorCourseExam::getUserId,loginUserId)
                            .in(TUserMajorCourseExam::getMajorCourseExamId, majorCourseExamIds));
                    if(!CollectionUtils.isEmpty(userMajorCourseExamList)){
                        for(TUserMajorCourseExam userMajorCourseExam:userMajorCourseExamList){
                            userMajorCourseExamMap.put(userMajorCourseExam.getMajorCourseExamId(),userMajorCourseExam);
                        }
                    }
                }
            }

            for(UserMajor userMajor:userMajorList){
                FrontUserMajor frontUserMajor = new FrontUserMajor();
                BeanUtils.copyProperties(userMajor,frontUserMajor);
                FrontMajor frontMajor = new FrontMajor();
                Major major = majorMap.get(userMajor.getMajorId());
                if(major != null){
                    BeanUtils.copyProperties(major,frontMajor);
                }
                frontUserMajor.setFrontMajor(frontMajor);
                List<FrontCourse> frontCourseList = new ArrayList<>();
                List<MajorCourse> majorCourses = majorCourseMap.get(userMajor.getMajorId());
                if(!CollectionUtils.isEmpty(majorCourses)){
                    for(MajorCourse majorCourse:majorCourses){
                        Course course = courseMap.get(majorCourse.getCourseId());
                        FrontCourse frontCourse = new FrontCourse();
                        if(course != null){
                            BeanUtils.copyProperties(course,frontCourse);
                            frontCourse.setSemester(majorCourse.getSemester());
                            frontCourse.setMajorId(majorCourse.getMajorId());
                            frontCourse.setHasExamCorrect(0);
                            //所有的考试
                            List<TMajorCourseExam> tMajorCourseExams = majorCourseExamAllMap.get(majorCourse.getId());
                            if(!CollectionUtils.isEmpty(tMajorCourseExams)){
                                List<FrontUserMajorCourseExam> _list = new ArrayList<>();
                                for(TMajorCourseExam majorCourseExam:tMajorCourseExams){
                                    TUserMajorCourseExam tUserMajorCourseExam = userMajorCourseExamMap.get(majorCourseExam.getMajorCourseExamId());
                                    if(tUserMajorCourseExam != null){
                                        FrontUserMajorCourseExam item = new FrontUserMajorCourseExam();
                                        BeanUtils.copyProperties(tUserMajorCourseExam,item);
                                        if(item.getStatus().equals(0) || item.getStatus().equals(1)){
                                            frontCourse.setHasExamCorrect(1);
                                        }
                                        _list.add(item);
                                    }
                                }
                                frontCourse.setUserMajorCourseExamList(_list);
                            }

                            UserCourse userCourse = userCourseMap.get(majorCourse.getMajorId() + "_" + frontCourse.getCourseId());
                            if(userCourse != null){
                                frontCourse.setLearingProgress(userCourse.getLearingProgress());
                                frontCourse.setExamScore(userCourse.getExamScore());
                                frontCourse.setTotalScore(userCourse.getTotalScore());
                                frontCourse.setLearingScore(userCourse.getLearingScore());
                                frontCourse.setHasExam(1);
                                //专业考试
                                TMajorCourseExam tMajorCourseExam = majorCourseExamMap.get(majorCourse.getId());
                                if(tMajorCourseExam != null){
                                    //用户考试记录
                                    TUserMajorCourseExam tUserMajorCourseExam = userMajorCourseExamMap.get(tMajorCourseExam.getMajorCourseExamId());
                                    //在在考试
                                    if(tUserMajorCourseExam !=null && tUserMajorCourseExam.getStatus().equals(0)){
                                        frontCourse.setHasExam(2);
                                    }else{
                                        if(tUserMajorCourseExam == null){//当前没有做过
                                           if(userCourse.getExamScore() == null) {//没有考试成绩
                                               frontCourse.setHasExam(2);
                                           } else if(userCourse.getTotalScore()!=null && userCourse.getTotalScore() < 60) {//总成绩小于60分
                                                frontCourse.setHasExam(3);
                                            }
                                        }
                                    }
                                    if(!frontCourse.getHasExam().equals(1)){
                                        frontCourse.setStartTime(tMajorCourseExam.getStartTime());
                                        frontCourse.setEndTime(tMajorCourseExam.getEndTime());
                                    }
                                }
                            }

                            frontCourseList.add(frontCourse);
                        }
                    }
                }
                frontUserMajor.setFrongCourseList(frontCourseList);
                frontUserMajor.setScoreRule(scoreRuleList.get(0));
                list.add(frontUserMajor);
            }
        }
        return success(list);
    }

    @OperationLog
    @Operation(method="exam",description= "开始考试")
    @GetMapping(value = "/exam/{majorId}/{courseId}")
    public ApiResult<TMajorCourseExam> exam(@PathVariable("majorId") Integer majorId,@PathVariable("courseId") Integer courseId) {
        TMajorCourseExam tMajorCourseExam = null;

        Integer loginUserId = getLoginUserId();
        UserCourse userCourse = userCourseService.getOne(new LambdaQueryWrapper<UserCourse>()
                        .select(UserCourse::getMajorId,UserCourse::getUserId,UserCourse::getCourseId,UserCourse::getExamScore,UserCourse::getLearingScore)
                .eq(UserCourse::getMajorId, majorId).eq(UserCourse::getCourseId, courseId)
                .eq(UserCourse::getUserId,loginUserId));
        if(userCourse == null){
            throw new BusinessException("无课程权限");
        }

        if(userCourse.getLearingScore() == null || userCourse.getLearingScore()<90){
            throw new BusinessException("学习成绩大于等于90分，才可以进行考试");
        }

        MajorCourse majorCourse = majorCourseService.getOne(new LambdaQueryWrapper<MajorCourse>()
                .select(MajorCourse::getMajorId,MajorCourse::getCourseId,MajorCourse::getId)
                .eq(MajorCourse::getMajorId, majorId)
                .eq(MajorCourse::getCourseId, courseId));

        if(majorCourse != null){
            List<TMajorCourseExam> tMajorCourseExams = tMajorCourseExamService.list(new LambdaQueryWrapper<TMajorCourseExam>()
                            .select(TMajorCourseExam::getStartTime,TMajorCourseExam::getMajorCourseExamId,TMajorCourseExam::getPaperId)
                    .eq(TMajorCourseExam::getMajorCourseId, majorCourse.getId())
                    .gt(TMajorCourseExam::getEndTime, new Date())
                    .orderByDesc(TMajorCourseExam::getEndTime));
            if(!CollectionUtils.isEmpty(tMajorCourseExams)){
                //专业课程考试
                tMajorCourseExam = tMajorCourseExams.get(tMajorCourseExams.size() - 1);
                Date nowDate = new Date();
                if(tMajorCourseExam.getStartTime().after(nowDate)){
                    throw new BusinessException("当前课程考试还未开始");
                }

                //用户考试记录
                TUserMajorCourseExam one = tUserMajorCourseExamService.getOne(new LambdaQueryWrapper<TUserMajorCourseExam>()
                                .select(TUserMajorCourseExam::getId,TUserMajorCourseExam::getStatus,TUserMajorCourseExam::getMajorCourseExamId,TUserMajorCourseExam::getPaperId,TUserMajorCourseExam::getScore)
                        .eq(TUserMajorCourseExam::getMajorCourseExamId, tMajorCourseExam.getMajorCourseExamId())
                        .eq(TUserMajorCourseExam::getUserId,loginUserId));
                //用户作答
                if(one != null){
                    if(one.getStatus().equals(1)){
                        throw new BusinessException("当前课程考试已经提交，等待批改。。。");
                    }
                    if(one.getStatus().equals(2)){
                        throw new BusinessException("当前课程考试已经提交，得分："+one.getScore());
                    }
                    one.setScore(null);
                    List<TUserMajorCourseExamItem> list = tUserMajorCourseExamItemService.list(new LambdaQueryWrapper<TUserMajorCourseExamItem>()
                                    .select(TUserMajorCourseExamItem::getId,TUserMajorCourseExamItem::getPaperQuestionId,TUserMajorCourseExamItem::getAnswer)
                                    .eq(TUserMajorCourseExamItem::getMajorCourseExamId, tMajorCourseExam.getMajorCourseExamId())
                                    .eq(TUserMajorCourseExamItem::getUserId,loginUserId));

                    one.setItemList(list);
                }else{
                    //没有做过当前试卷，总成绩如果大于60分，就不用再考了
                    if(userCourse.getTotalScore() != null && userCourse.getTotalScore()>=60){
                        throw new BusinessException("课程成绩已通过，总成绩："+userCourse.getTotalScore());
                    }

                    one = new TUserMajorCourseExam();
                    one.setMajorCourseExamId(tMajorCourseExam.getMajorCourseExamId());
                    one.setPaperId(tMajorCourseExam.getPaperId());
                    one.setStartTime(nowDate);
                }

                TPaper byId = tPaperService.getOne(new LambdaQueryWrapper<TPaper>()
                                .select(TPaper::getPaperName,TPaper::getPaperFile)
                        .eq(TPaper::getPaperId,tMajorCourseExam.getPaperId()));
                if(byId != null){
                    List<TPaperQuestion> qlist = tPaperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>()
                                    .select(TPaperQuestion::getPaperQuestionId,TPaperQuestion::getQuestionGroup,TPaperQuestion::getQuestionType,TPaperQuestion::getQuestionTitle,TPaperQuestion::getQuestionOptions,TPaperQuestion::getQuestionScore,TPaperQuestion::getQuestionSort)
                                    .eq(TPaperQuestion::getPaperId, tMajorCourseExam.getPaperId()));
                    byId.setPaperQuestionList(qlist);
                    byId.setUserMajorCourseExam(one);
                    tMajorCourseExam.setPaper(byId);
                }else{
                    throw new BusinessException("试卷未找到");
                }
            }else{
                throw new BusinessException("未发布考试");
            }
        }else{
            throw new BusinessException("未找到专业课程");
        }
        return success(tMajorCourseExam);
    }


    @OperationLog
    @Operation(method="submitPaper",description="试卷提交")
    @PostMapping(value = "/submitPaper")
    public ApiResult<?> submitPaper(@RequestBody TUserMajorCourseExam param) {
        Integer loginUserId = getLoginUserId();
        param.setUserId(loginUserId);

        if(param.getMajorCourseExamId() == null){
            throw new BusinessException("专业课程考试不能为空");
        }
        if(param.getStatus() == null){
            throw new BusinessException("考试状态不能为空");
        }
        if(!param.getStatus().equals(0) && !param.getStatus().equals(1)){
            throw new BusinessException("考试状态错误");
        }
        TMajorCourseExam byId = tMajorCourseExamService.getById(param.getMajorCourseExamId());
        if(byId == null){
            throw new BusinessException("专业课程考试不存在");
        }

        Date nowDate = new Date();
        if(byId.getEndTime().before(nowDate) || byId.getStartTime().after(nowDate)){
            throw new BusinessException("专业课程考试未开始或者已结束");
        }

        MajorCourse byId1 = majorCourseService.getById(byId.getMajorCourseId());
        if(byId1 == null){
            throw new BusinessException("专业课程未找到");
        }

        long count1 = userCourseService.count(new LambdaQueryWrapper<UserCourse>()
                .eq(UserCourse::getMajorId, byId1.getMajorId()).eq(UserCourse::getCourseId, byId1.getCourseId())
                .eq(UserCourse::getUserId,loginUserId));
        if(count1 == 0){
            throw new BusinessException("无课程权限");
        }
        param.setCourseId(byId1.getCourseId());
        param.setMajorId(byId1.getMajorId());
        param.setPaperId(byId.getPaperId());
        param.setEndTime(nowDate);
        param.setScore(null);
        tUserMajorCourseExamService.submitPaper(param);
        return success("添加成功");
    }

    @OperationLog
    @Operation(method="coursewareList",description="查询全部章节课时信息")
    @GetMapping(value = "/courseware/{majorId}/{courseId}")
    public ApiResult<List<FrontCourseware>> coursewareList(@PathVariable("majorId") Integer majorId,@PathVariable("courseId") Integer courseId) {
        Integer loginUserId = getLoginUserId();

        long count1 = userCourseService.count(new LambdaQueryWrapper<UserCourse>()
                .eq(UserCourse::getMajorId, majorId).eq(UserCourse::getCourseId, courseId)
                .eq(UserCourse::getUserId,loginUserId));
        if(count1 == 0){
            throw new BusinessException("无课程权限");
        }

        ThreadLocalContextHolder.setCloseTenant(true);
        List<Courseware> list = coursewareService.list(new LambdaQueryWrapper<Courseware>().eq(Courseware::getCourseId, courseId));
        ThreadLocalContextHolder.removeCloseTenant();

        List<FrontCourseware> _list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            Integer lastPlayCoursewareId = null;
            List<UserLearning> userLearningList = userLearningService.list(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getUserId, loginUserId)
                    .eq(UserLearning::getMajorId, majorId).eq(UserLearning::getCourseId, courseId)
                    .orderByDesc(UserLearning::getUpdateTime));
            Map<Integer,UserLearning> userLearningMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(userLearningList)){
                lastPlayCoursewareId = userLearningList.get(0).getCoursewareId();
                for(UserLearning userLearning:userLearningList){
                    userLearningMap.put(userLearning.getCoursewareId(),userLearning);
                }
            }

            for(Courseware courseware:list){
                FrontCourseware frontCourseware = new FrontCourseware();
                BeanUtils.copyProperties(courseware,frontCourseware);
                if(frontCourseware.getCoursewareId().equals(lastPlayCoursewareId)){
                    frontCourseware.setLastPlay(true);
                }
                UserLearning userLearning = userLearningMap.get(courseware.getCoursewareId());
                if(userLearning != null){
                    frontCourseware.setCurrentPlayTime(userLearning.getCurrentPlayTime());
                    frontCourseware.setTotalPalyTime(userLearning.getTotalPalyTime());
                    frontCourseware.setProgress(userLearning.getPlatProgress());
                }else{
                    frontCourseware.setProgress(0);
                    frontCourseware.setTotalPalyTime(0);
                    frontCourseware.setCurrentPlayTime(0);
                }
                _list.add(frontCourseware);
            }
        }

        return success(_list);
    }

    @OperationLog
    @Operation(method="userLearning",description="学习时长记录")
    @PostMapping(value = "/userLearning")
    public ApiResult<?> userLearning(@RequestBody UserLearning param) {
        Integer loginUserId = getLoginUserId();
        if(param.getMajorId() == null){
            throw new BusinessException("专业不能为空");
        }
        if(param.getCourseId() == null){
            throw new BusinessException("课程不能为空");
        }
        if(param.getCoursewareId() == null){
            throw new BusinessException("课件不能为空");
        }
        if(param.getCurrentPlayTime() == null){
            throw new BusinessException("当前播放时长不能为空");
        }
        if(param.getTime() == null || param.getTime()>300 || param.getTime()<0){
            throw new BusinessException("观看时长无效");
        }

        long count_1 = userCourseService.count(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getMajorId, param.getMajorId()).eq(UserCourse::getCourseId, param.getCourseId()).eq(UserCourse::getUserId,loginUserId));
        if(count_1 == 0){
            throw new BusinessException("无课程权限");
        }

        ThreadLocalContextHolder.setCloseTenant(true);
        Courseware courseware = coursewareService.getById(param.getCoursewareId());
        ThreadLocalContextHolder.removeCloseTenant();
        if(courseware.getDuration() == null){
            throw new BusinessException("课件没有时长");
        }

        Integer currentPlayTime = param.getCurrentPlayTime();
        if(currentPlayTime > courseware.getDuration() || currentPlayTime < 0){
            throw new BusinessException("当前播放时长错误");
        }

        String _key = CommonUtil.stringJoiner(loginUserId.toString(),param.getMajorId().toString(),param.getCourseId().toString(),param.getCoursewareId().toString());
        RLock lock = redissonClient.getLock(_key);
        try {
            // 拿锁失败时会不停的重试
            // 没有Watch Dog ，获取锁后10s自动释放,防止死锁
            lock.lock(10, TimeUnit.SECONDS);

            UserLearning one = userLearningService.getOne(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getUserId, loginUserId)
                    .eq(UserLearning::getMajorId, param.getMajorId()).eq(UserLearning::getCourseId, param.getCourseId())
                    .eq(UserLearning::getCoursewareId, param.getCoursewareId()));
            if(one != null){
                Integer totalPalyTime = one.getTotalPalyTime()+param.getTime();

                //计算播放完成次数和当前播放时间
                if((courseware.getDuration() - currentPlayTime) <= 20){
                    one.setPlatProgress(100);
                    one.setCurrentPlayTime(0);
                }else{
                    int p =(int)(currentPlayTime/(courseware.getDuration()*1.0f)*100);
                    if(p>one.getPlatProgress()){
                        one.setPlatProgress(p);
                    }
                    one.setCurrentPlayTime(currentPlayTime);
                }

                one.setTotalPalyTime(totalPalyTime);
                one.setUpdateTime(null);
                userLearningService.updateById(one);
            }else{
                UserLearning userLearning = new UserLearning();
                userLearning.setUserId(loginUserId);
                userLearning.setMajorId(param.getMajorId());
                userLearning.setCourseId(param.getCourseId());
                userLearning.setCoursewareId(param.getCoursewareId());
                userLearning.setTotalPalyTime(param.getTime());

                //计算播放完成次数和当前播放时间
                if((courseware.getDuration() - currentPlayTime) <= 20 ){
                    userLearning.setPlatProgress(100);
                    userLearning.setCurrentPlayTime(0);
                }else{
                    int p =(int)(currentPlayTime/(courseware.getDuration()*1.0f)*100);
                    userLearning.setPlatProgress(p);
                    userLearning.setCurrentPlayTime(currentPlayTime);
                }

                userLearningService.save(userLearning);
            }
        }finally {
            //lock.isHeldByCurrentThread() 判断当前线程是否持有锁
            if(null != lock && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return success("添加成功");
    }

    @Operation(method="userLearning",description="下载登记表")
    @GetMapping(value = "/download/register/{majorId}")
    public ApiResult<?> register(@PathVariable("majorId") Integer majorId, HttpServletResponse response) throws Exception {
        User loginUser = getLoginUser();
        WordOperation wordOperation = userMajorService.generateSingleRegistrationFile(majorId,loginUser);
        wordOperation.print(response,"学生登记表_" + loginUser.getRealname() +loginUser.getIdCard()+".doc");
        return null;
    }

    @Operation(method="enrollment",description="下载学籍表")
    @GetMapping(value = "/download/enrollment/{majorId}")
    public ApiResult<?> enrollment(@PathVariable("majorId") Integer majorId, HttpServletResponse response) throws Exception {
        User loginUser = getLoginUser();
        WordOperation wordOperation = userMajorService.generateSingleEnrollmentFile(majorId,loginUser);
        wordOperation.print(response,"学生学籍表_" + loginUser.getRealname() +loginUser.getIdCard()+".doc");
        return null;
    }

    @OperationLog
    @Operation(method="listUserCourses",description="查询用户与课程关系")
    @PostMapping("/listUserCourses")
    public ResponseEntity<UserCourseResponse> listUserCourses(@RequestBody UserCourseParam param) {
        User loginUser = getLoginUser();

        // 查询用户表
        User user = userService.getById(loginUser.getUserId());

        // 查询用户专业表
        UserMajor userMajor = userMajorService.getOne(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getUserId, loginUser.getUserId())
                .eq(UserMajor::getMajorId, param.getMajorId()));
        Major major = majorService.getById(param.getMajorId());
        if (major != null) {
            userMajor.setMajor(major);
        }
        Org org = orgService.getById(userMajor.getOrgId());
        if (org != null) {
            userMajor.setOrg(org);
        }

        // 查询用户课程表
        param.setUserId(loginUser.getUserId());
        List<UserCourse> userCourseList = userCourseService.listRel(param,false);

        UserCourseResponse response = new UserCourseResponse();
        response.setUser(user);
        response.setUserMajor(userMajor);
        response.setUserCourseList(userCourseList);

        return ResponseEntity.ok(response);
    }

    @Operation(method="updateThesis",description="修改用户论文信息")
    @PostMapping(value = "/updateThesis")
    public ApiResult<?> update(@RequestBody UserMajor studentMajor) {
        if(studentMajor.getMajorId() == null){
            return fail("专业不能为空");
        }
        User loginUser = getLoginUser();

        //用户的专业
        UserMajor userMajor= userMajorService.getOne(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getUserId, loginUser.getUserId())
                .eq(UserMajor::getMajorId, studentMajor.getMajorId()));

        if(userMajor == null){
            throw new BusinessException("用户不存在当前专业");
        }

        if(StringUtils.hasText(userMajor.getThesisScore())){
            throw new BusinessException("已经有论文成绩了，不能修改");
        }

        userMajorService.update(new LambdaUpdateWrapper<UserMajor>()
                        .set(UserMajor::getThesisName,studentMajor.getThesisName())
                     .set(UserMajor::getThesisFile,studentMajor.getThesisFile())
                .eq(UserMajor::getId,userMajor.getId()));

        return success("修改成功");
    }
}

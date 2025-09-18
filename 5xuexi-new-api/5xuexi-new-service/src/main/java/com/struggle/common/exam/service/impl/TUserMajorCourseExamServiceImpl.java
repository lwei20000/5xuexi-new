package com.struggle.common.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.*;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.base.service.*;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.entity.TPaperQuestion;
import com.struggle.common.exam.entity.TUserMajorCourseExam;
import com.struggle.common.exam.entity.TUserMajorCourseExamItem;
import com.struggle.common.exam.mapper.TUserMajorCourseExamMapper;
import com.struggle.common.exam.param.TUserMajorCourseExamParam;
import com.struggle.common.exam.service.ITPaperQuestionService;
import com.struggle.common.exam.service.ITUserMajorCourseExamItemService;
import com.struggle.common.exam.service.ITUserMajorCourseExamService;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.OrgService;
import com.struggle.common.system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: TUserMajorCourseExamService
 * @Description:  用户专业考试记录-ServiceImpl层
 * @author xsk
 */
@Service
public class TUserMajorCourseExamServiceImpl extends ServiceImpl<TUserMajorCourseExamMapper, TUserMajorCourseExam> implements  ITUserMajorCourseExamService {

    @Resource
    private TUserMajorCourseExamMapper tUserMajorCourseExamMapper;

    @Resource
    private ITUserMajorCourseExamItemService userMajorCourseExamItemService;

    @Resource
    private ITPaperQuestionService paperQuestionService;

    @Resource
    private MajorService majorService;

    @Resource
    private MajorCourseService majorCourseService;

    @Resource
    private UserService userService;
    @Resource
    private CourseService courseService;
    @Resource
    private OrgService orgService;
    @Resource
    private UserCourseService userCourseService;

    @Resource
    private ScoreRuleService scoreRuleService;

    @Override
    @Transactional
    public void submitPaper(TUserMajorCourseExam param) {
        if(param.getId() != null){
            TUserMajorCourseExam tUserMajorCourseExam = tUserMajorCourseExamMapper.selectById(param.getId());
            if(tUserMajorCourseExam != null){
                if(!tUserMajorCourseExam.getMajorCourseExamId().equals(param.getMajorCourseExamId())
                   || !tUserMajorCourseExam.getUserId().equals(param.getUserId())
                   || !tUserMajorCourseExam.getPaperId().equals(param.getPaperId())){
                    throw new BusinessException("数据对应关系错误");
                }
            }else{
                throw new BusinessException("数据对应错误");
            }
        }

        TUserMajorCourseExam tUserMajorCourseExams = tUserMajorCourseExamMapper.selectOne(new LambdaQueryWrapper<TUserMajorCourseExam>()
                .eq(TUserMajorCourseExam::getUserId, param.getUserId())
                .eq(TUserMajorCourseExam::getMajorCourseExamId, param.getMajorCourseExamId()));
        if(tUserMajorCourseExams != null){
            if(param.getId() !=null && !tUserMajorCourseExams.getId().equals(param.getId())){
                throw new BusinessException("数据对应关系错误");
            }
            if(!tUserMajorCourseExams.getStatus().equals(0)){
                throw new BusinessException("试卷已经提交过了，不能重复作答");
            }
        }
        //id不存在，但是可以找到历史作答
        if(param.getId() == null && tUserMajorCourseExams != null){
            param.setId(tUserMajorCourseExams.getId());
        }

        /**
         * 删除历史作答
         */
        userMajorCourseExamItemService.remove(new LambdaQueryWrapper<TUserMajorCourseExamItem>()
                .eq(TUserMajorCourseExamItem::getMajorCourseExamId,param.getMajorCourseExamId())
                .eq(TUserMajorCourseExamItem::getUserId,param.getUserId()));

        boolean flag = false;
        Map<Integer,TPaperQuestion> questionMap = new HashMap<>();
        if(param.getStatus().equals(1)){
            //试卷题目
            flag = true;
            List<TPaperQuestion> list = paperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>().eq(TPaperQuestion::getPaperId, param.getPaperId()));
            if(!CollectionUtils.isEmpty(list)){
                for(TPaperQuestion question:list){
                    questionMap.put(question.getPaperQuestionId(),question);
                    if(question.getQuestionType().equals(5)){
                        flag = false;
                        break;
                    }
                }
            }
        }

        /**
         * 作答
         */
        List<TUserMajorCourseExamItem> itemList = param.getItemList();
        int totalScore = 0;
        if(!CollectionUtils.isEmpty(itemList)){
            for(TUserMajorCourseExamItem userMajorCourseExamItem:itemList){
                userMajorCourseExamItem.setId(null);
                userMajorCourseExamItem.setUserId(param.getUserId());
                userMajorCourseExamItem.setMajorCourseExamId(param.getMajorCourseExamId());
                if(flag){//自动判卷
                    TPaperQuestion tPaperQuestion = questionMap.get(userMajorCourseExamItem.getPaperQuestionId());
                    int score = 0;
                    if(tPaperQuestion != null){
                        if(tPaperQuestion.getQuestionType().equals(1) || tPaperQuestion.getQuestionType().equals(2)){
                            if(StringUtils.hasText(userMajorCourseExamItem.getAnswer()) && userMajorCourseExamItem.getAnswer().toUpperCase().equals(tPaperQuestion.getQuestionAnswer().toUpperCase())){
                                score = tPaperQuestion.getQuestionScore();
                            }
                        }else if(tPaperQuestion.getQuestionType().equals(3) || tPaperQuestion.getQuestionType().equals(4)){
                            if(StringUtils.hasText(userMajorCourseExamItem.getAnswer()) && userMajorCourseExamItem.getAnswer().equals(tPaperQuestion.getQuestionAnswer())){
                                score = tPaperQuestion.getQuestionScore();
                            }
                        }
                    }
                    totalScore += score;
                    userMajorCourseExamItem.setScore(score);
                }
            }
            if(flag){//自动判卷
                param.setScore(totalScore);
                param.setStatus(2);
            }
            userMajorCourseExamItemService.saveBatch(itemList);
        }

        if(param.getId() !=null){
            param.setUpdateTime(null);
            tUserMajorCourseExamMapper.updateById(param);
        }else{
            tUserMajorCourseExamMapper.insert(param);
        }
        if(flag){
            this.updateTotalScore(totalScore,param.getUserId(),param.getMajorId(),param.getCourseId());
        }
    }

    @Override
    @Transactional
    public void correctPaper(TUserMajorCourseExam param,List<Integer> orgs) {

        TUserMajorCourseExamParam _param = new TUserMajorCourseExamParam();
        //增加机构范围
        _param.setOrgIds(orgs);
        _param.setId(param.getId());
        List<TUserMajorCourseExam> tUserMajorCourseExams = baseMapper.selectListRel(_param);
        if(CollectionUtils.isEmpty(tUserMajorCourseExams)){
            throw new BusinessException("无权限批改这份试卷");
        }

        TUserMajorCourseExam tUserMajorCourseExam = tUserMajorCourseExams.get(0);

        int totalScore = 0;

        //批改分数
        List<TUserMajorCourseExamItem> itemList = param.getItemList();
        Map<Integer,Integer> scoreMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(itemList)){
            for(TUserMajorCourseExamItem userMajorCourseExamItem:itemList){
                scoreMap.put(userMajorCourseExamItem.getPaperQuestionId(),userMajorCourseExamItem.getScore());
            }
        }


        //试卷题目
        List<TPaperQuestion> list = paperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>().eq(TPaperQuestion::getPaperId, tUserMajorCourseExam.getPaperId()));
        Map<Integer,TPaperQuestion> questionMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(list)){
            for(TPaperQuestion question:list){
                questionMap.put(question.getPaperQuestionId(),question);
            }
        }

        //用户作答
        List<TUserMajorCourseExamItem> answerList = userMajorCourseExamItemService.list(new LambdaQueryWrapper<TUserMajorCourseExamItem>()
                .eq(TUserMajorCourseExamItem::getMajorCourseExamId, tUserMajorCourseExam.getMajorCourseExamId())
                .eq(TUserMajorCourseExamItem::getUserId, tUserMajorCourseExam.getUserId()));
       if(!CollectionUtils.isEmpty(answerList)){
           for(TUserMajorCourseExamItem userMajorCourseExamItem:answerList){
               TPaperQuestion tPaperQuestion = questionMap.get(userMajorCourseExamItem.getPaperQuestionId());
               int score = 0;
               if(tPaperQuestion != null){
                  if(tPaperQuestion.getQuestionType().equals(1) || tPaperQuestion.getQuestionType().equals(2)){
                      if(StringUtils.hasText(userMajorCourseExamItem.getAnswer()) && userMajorCourseExamItem.getAnswer().toUpperCase().equals(tPaperQuestion.getQuestionAnswer().toUpperCase())){
                          score = tPaperQuestion.getQuestionScore();
                      }
                  }else if(tPaperQuestion.getQuestionType().equals(3) || tPaperQuestion.getQuestionType().equals(4)){
                      if(StringUtils.hasText(userMajorCourseExamItem.getAnswer()) && userMajorCourseExamItem.getAnswer().equals(tPaperQuestion.getQuestionAnswer())){
                          score = tPaperQuestion.getQuestionScore();
                      }
                  }else { //主观题
                      Integer _score = scoreMap.get(tPaperQuestion.getPaperQuestionId());
                      if(_score ==null || _score<0){
                          score = 0;
                      }else if(_score>tPaperQuestion.getQuestionScore()){
                          score = tPaperQuestion.getQuestionScore();
                      }else{
                          score = _score;
                      }
                  }
               }
               totalScore += score;
               userMajorCourseExamItem.setScore(score);
           }
           userMajorCourseExamItemService.updateBatchById(answerList);
       }
       tUserMajorCourseExamMapper.update(null,new LambdaUpdateWrapper<TUserMajorCourseExam>()
                        .set(TUserMajorCourseExam::getScore,totalScore)
                        .set(TUserMajorCourseExam::getStatus,2)
                        .eq(TUserMajorCourseExam::getId,tUserMajorCourseExam.getId()));

        this.updateTotalScore(totalScore,tUserMajorCourseExam.getUserId(),tUserMajorCourseExam.getMajorId(),tUserMajorCourseExam.getCourseId());
    }

    private void  updateTotalScore(int totalScore,Integer userId,Integer majorId,Integer courseId){
        UserCourse userCourse = userCourseService.getOne(new LambdaQueryWrapper<UserCourse>()
                .eq(UserCourse::getUserId, userId)
                .eq(UserCourse::getMajorId, majorId)
                .eq(UserCourse::getCourseId, courseId));
        if(userCourse != null){
            if(userCourse.getExamScore() == null ||  userCourse.getExamScore()<totalScore){
                //更新课表成绩
                List<ScoreRule> scoreRuleList = scoreRuleService.list();
                if(!CollectionUtils.isEmpty(scoreRuleList)){
                    ScoreRule scoreRule = scoreRuleList.get(0);
                    int _totalScore = 0;
                    //计算总成绩
                    float l_score = userCourse.getLearingScore()*(scoreRule.getLearningScoreProportions()/100.0f);
                    float k_score = totalScore*(scoreRule.getExamScoreProportions()/100.0f);
                    if((l_score+k_score)>=100){
                        _totalScore  = 100;
                    }else{
                        _totalScore = (int)(l_score+k_score);
                    }

                    userCourseService.update(null,new LambdaUpdateWrapper<UserCourse>()
                            .set(UserCourse::getExamScore,totalScore)
                            .set(UserCourse::getTotalScore,_totalScore)
                            .eq(UserCourse::getId, userCourse.getId()));
                }
            }
        }
    }

    @Override
    public PageResult<TUserMajorCourseExam> pageRel(TUserMajorCourseExamParam param) {
        PageParam<TUserMajorCourseExam, TUserMajorCourseExamParam> page = new PageParam<TUserMajorCourseExam, TUserMajorCourseExamParam>(param);
        page.setDefaultOrder("update_time desc");
        List<TUserMajorCourseExam> list = new ArrayList<>();
        if(param.getStatus() != null && param.getStatus() == 3){
            list = baseMapper.selectPageRelNotExam(page,param);
        }else{
            list = baseMapper.selectPageRel(page, param);
        }

        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<TUserMajorCourseExam> listRel(TUserMajorCourseExamParam param,boolean details) {
        List<TUserMajorCourseExam> list = new ArrayList<>();
        if(param.getStatus() != null && param.getStatus() == 3){
            list = baseMapper.selectListRelNotExam(param);
        }else{
            list = baseMapper.selectListRel(param);
        }
        if(details){
            this.initOther(list);
        }
        // 排序
        PageParam<TUserMajorCourseExam, TUserMajorCourseExamParam> page = new PageParam<>();
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    private void initOther(List<TUserMajorCourseExam> list){
        if(!CollectionUtils.isEmpty(list)){
            Set<Integer> courseIds = new HashSet<>();
            Set<Integer> userIds = new HashSet<>();
            Set<Integer> majorIds = new HashSet<>();
            Set<Integer> orgIds = new HashSet<>();
            for (TUserMajorCourseExam userMajorCourseExam : list) {
                courseIds.add(userMajorCourseExam.getCourseId());
                userIds.add(userMajorCourseExam.getUserId());
                majorIds.add(userMajorCourseExam.getMajorId());
                if(userMajorCourseExam.getOrgId() !=null){
                    orgIds.add(userMajorCourseExam.getOrgId());
                }
            }
            List<Major> majors = majorService.listByIds(majorIds);
            Map<Integer, Major> majorMap= new HashMap<>();
            if(!CollectionUtils.isEmpty(majors)){
                majorMap = majors.stream().collect(Collectors.toMap(Major::getMajorId, Major -> Major));
            }

            List<User> users = userService.listByIds(userIds);
            Map<Integer, User> userMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(users)) {
                userMap = users.stream().collect(Collectors.toMap(User::getUserId, User -> User));
            }
            CourseParam c = new CourseParam();
            c.setCourseIds(new ArrayList<>(courseIds));
            List<Course> courses = courseService.listRel(c);
            Map<Integer, Course> courseMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(courses)) {
                courseMap = courses.stream().collect(Collectors.toMap(Course::getCourseId, Course -> Course));
            }

            Map<Integer, Org> orgMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(orgIds)){
                List<Org> orgs = orgService.listByIds(orgIds);
                if (!CollectionUtils.isEmpty(orgs)) {
                    orgMap = orgs.stream().collect(Collectors.toMap(Org::getOrgId, Org -> Org));
                }
            }

            for (TUserMajorCourseExam userMajorCourseExam : list) {
                userMajorCourseExam.setMajor(majorMap.get(userMajorCourseExam.getMajorId()));
                userMajorCourseExam.setCourse(courseMap.get(userMajorCourseExam.getCourseId()));
                User user = userMap.get(userMajorCourseExam.getUserId());
                if(user!=null){
                    user.setPassword(null);
                    userMajorCourseExam.setUser(user);
                }
                if(userMajorCourseExam.getOrgId() !=null){
                    userMajorCourseExam.setOrg(orgMap.get(userMajorCourseExam.getOrgId()));
                }
            }
        }
    }

    @Override
    @Transactional
    public void batchSubmitPaper(List<TMajorCourseExam> records,ScoreRule scoreRule){
        if(!CollectionUtils.isEmpty(records)) {
            Set<Integer> examIds = new HashSet<>();
            Set<Integer> majorCourseIdIds = new HashSet<>();
            Map<Integer,Integer> majorCourseExamMap = new HashMap<>();
            for (TMajorCourseExam exam : records) {
                examIds.add(exam.getMajorCourseExamId());
                majorCourseIdIds.add(exam.getMajorCourseId());
                majorCourseExamMap.put(exam.getMajorCourseExamId(),exam.getMajorCourseId());
            }
            List<TUserMajorCourseExam> list = this.list(new LambdaQueryWrapper<TUserMajorCourseExam>()
                    .eq(TUserMajorCourseExam::getStatus, 0)
                    .in(TUserMajorCourseExam::getMajorCourseExamId, examIds));
            if(!CollectionUtils.isEmpty(list)){
                Set<Integer> paperIds = new HashSet<>();
                Set<Integer> userIds = new HashSet<>();
                Set<Integer> majorCourseExamIds = new HashSet<>();
                for (TUserMajorCourseExam userExam : list) {
                    majorCourseExamIds.add(userExam.getMajorCourseExamId());
                    userIds.add(userExam.getUserId());
                    paperIds.add(userExam.getPaperId());
                }

                //试卷题目
                Set<Integer> removePaperIds = new HashSet<>();
                Map<String, TPaperQuestion> questionMap = new HashMap<>();
                List<TPaperQuestion> questionList = paperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>().in(TPaperQuestion::getPaperId, paperIds));
                if(!CollectionUtils.isEmpty(questionList)){
                    for(TPaperQuestion question:questionList){
                        if(question.getQuestionType().equals(5)){//过滤有主观题的试卷
                            removePaperIds.add(question.getPaperId());
                            continue;
                        }
                        questionMap.put(question.getPaperId()+"_"+question.getPaperQuestionId(),question);
                    }
                }

                //移除不需要自动批改的试卷
                if(!CollectionUtils.isEmpty(removePaperIds)){
                    for(Integer pId: removePaperIds){
                        paperIds.remove(pId);
                    }
                }

                //专业课程
                List<MajorCourse> list1 = majorCourseService.list(new LambdaQueryWrapper<MajorCourse>()
                                .select(MajorCourse::getCourseId,MajorCourse::getMajorId)
                                .in(MajorCourse::getId, majorCourseIdIds));
                Set<Integer> courseIds = new HashSet<>();
                Set<Integer> majorIds = new HashSet<>();
                Map<Integer,MajorCourse> majorCourseMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(list1)){
                    for(MajorCourse majorCourse:list1){
                        majorIds.add(majorCourse.getMajorId());
                        courseIds.add(majorCourse.getCourseId());
                        majorCourseMap.put(majorCourse.getId(),majorCourse);
                    }
                }

                //课表
                Map<String,UserCourse> userScheduleMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(majorIds) && !CollectionUtils.isEmpty(courseIds) && !CollectionUtils.isEmpty(userIds) ){
                    List<UserCourse> userSchedules = userCourseService.list(new LambdaQueryWrapper<UserCourse>()
                            .in(UserCourse::getUserId, userIds)
                            .in(UserCourse::getCourseId, courseIds)
                            .in(UserCourse::getMajorId, majorIds));
                    if(!CollectionUtils.isEmpty(userSchedules)){
                        for (UserCourse userSchedule:userSchedules){
                            String key = userSchedule.getMajorId()+"_"+userSchedule.getCourseId()+"_"+userSchedule.getUserId();
                            userScheduleMap.put(key,userSchedule);
                        }
                    }
                }

                //作答
                Map<String, List<TUserMajorCourseExamItem>> userObjectExamItemMap = new HashMap<>();
                List<TUserMajorCourseExamItem> tTUserObjectExamItems = userMajorCourseExamItemService.list(new LambdaQueryWrapper<TUserMajorCourseExamItem>()
                        .in(TUserMajorCourseExamItem::getUserId, userIds)
                        .in(TUserMajorCourseExamItem::getMajorCourseExamId, majorCourseExamIds));
                if(!CollectionUtils.isEmpty(tTUserObjectExamItems)){
                    for(TUserMajorCourseExamItem userObjectExamItem:tTUserObjectExamItems){
                        String key = userObjectExamItem.getMajorCourseExamId()+"_"+userObjectExamItem.getUserId();
                        List<TUserMajorCourseExamItem> userObjectExamItems = userObjectExamItemMap.get(key);
                        if(!CollectionUtils.isEmpty(userObjectExamItems)){
                            userObjectExamItems.add(userObjectExamItem);
                        }else{
                            userObjectExamItems = new ArrayList<>();
                            userObjectExamItems.add(userObjectExamItem);
                            userObjectExamItemMap.put(key,userObjectExamItems);
                        }
                    }
                }

                List<TUserMajorCourseExam> updateExamList = new ArrayList<>();
                List<TUserMajorCourseExamItem> updateExamItemList = new ArrayList<>();
                List<UserCourse> updateScheduleList = new ArrayList<>();
                for (TUserMajorCourseExam userObjectExam : list) {
                    Integer paperId = userObjectExam.getPaperId();
                    if(paperIds.contains(paperId)){ //需要自动批卷
                        String key = userObjectExam.getMajorCourseExamId()+"_"+userObjectExam.getUserId();
                        List<TUserMajorCourseExamItem> userObjectExamItems = userObjectExamItemMap.get(key);
                        int totalScore = 0;
                        if(!CollectionUtils.isEmpty(userObjectExamItems)){
                            for(TUserMajorCourseExamItem userObjectExamItem:userObjectExamItems){
                                TPaperQuestion tPaperQuestion = questionMap.get(paperId+"_"+userObjectExamItem.getPaperQuestionId());
                                int score = 0;
                                if(tPaperQuestion != null){
                                    if(tPaperQuestion.getQuestionType().equals(1) || tPaperQuestion.getQuestionType().equals(2)){
                                        if(StringUtils.hasText(userObjectExamItem.getAnswer()) && userObjectExamItem.getAnswer().toUpperCase().equals(tPaperQuestion.getQuestionAnswer().toUpperCase())){
                                            score = tPaperQuestion.getQuestionScore();
                                        }
                                    }else if(tPaperQuestion.getQuestionType().equals(3) || tPaperQuestion.getQuestionType().equals(4)){
                                        if(StringUtils.hasText(userObjectExamItem.getAnswer()) && userObjectExamItem.getAnswer().equals(tPaperQuestion.getQuestionAnswer())){
                                            score = tPaperQuestion.getQuestionScore();
                                        }
                                    }
                                }
                                totalScore += score;
                                userObjectExamItem.setScore(score);
                                updateExamItemList.add(userObjectExamItem);
                            }
                        }
                        userObjectExam.setScore(totalScore);
                        userObjectExam.setStatus(2);

                        if(scoreRule != null){
                            Integer majorCourseId = majorCourseExamMap.get(userObjectExam.getMajorCourseExamId());
                            if(majorCourseId != null){
                                MajorCourse majorCourse = majorCourseMap.get(majorCourseId);
                                if(majorCourse != null){
                                    UserCourse userSchedule = userScheduleMap.get( majorCourse.getMajorId()+"_"+majorCourse.getCourseId()+"_"+userObjectExam.getUserId());
                                    if(userSchedule != null){
                                        int _totalScore = 0;
                                        //计算总成绩
                                        float l_score = userSchedule.getLearingScore()*(scoreRule.getLearningScoreProportions()/100.0f);
                                        float k_score = totalScore*(scoreRule.getExamScoreProportions()/100.0f);
                                        if((l_score+k_score)>=100){
                                            _totalScore  = 100;
                                        }else{
                                            _totalScore = (int)(l_score+k_score);
                                        }

                                        userSchedule.setExamScore(totalScore);
                                        userSchedule.setTotalScore(_totalScore);
                                        updateScheduleList.add(userSchedule);
                                    }
                                }
                            }
                        }
                    }else{ // 不需要自动批卷
                        userObjectExam.setStatus(1);
                    }
                    updateExamList.add(userObjectExam);
                }

                if(!CollectionUtils.isEmpty(updateExamList)){
                    this.updateBatchById(updateExamList,3000);
                }
                if(!CollectionUtils.isEmpty(updateExamItemList)){
                    userMajorCourseExamItemService.updateBatchById(updateExamItemList,3000);
                }
                if(!CollectionUtils.isEmpty(updateScheduleList)){
                    userCourseService.updateBatchById(updateScheduleList,3000);
                }
            }
        }
    }
    @Override
    public void dataExport(HttpServletResponse response, TUserMajorCourseExamParam param) {
        try {
            ExcelOperation entity = new ExcelOperation(ExcelConstant.EXCEL.COLLEGE_EXCEL_EXPORT_DATA.getCode());
            List<TUserMajorCourseExam> userMajorCourseExams = this.listRel(param,true);
            List<Map<String, Object>>  xxList = new ArrayList<>();
            String[] semesterArr = new String[]{"","一","二","三","四","五","六","七","八","九","十"};
            String[] statusArr = new String[]{"答题进行中","提交未批改","提交已批改","未考试"};
            if (!CollectionUtils.isEmpty(userMajorCourseExams)) {
                for (TUserMajorCourseExam userMajorCourseExam : userMajorCourseExams) {
                    Map<String, Object> map = new HashMap<>();
                    User user = userMajorCourseExam.getUser();
                    if(user !=null){
                        map.put("student",user.getRealname() +"_"+ user.getUsername());
                    }else{
                        map.put("student","");
                    }

                    Major major = userMajorCourseExam.getMajor();
                    if(major != null){
                        map.put("major",major.getMajorYear()+"_"+major.getMajorCode()+"_"+major.getMajorName()+"_"+major.getMajorType()+
                                "_"+major.getMajorGradation()+"_"+major.getMajorForms()+"_"+major.getMajorLength()+"年制");
                    }else{
                        map.put("major","");
                    }
                    Org org = userMajorCourseExam.getOrg();
                    if(org != null){
                        map.put("org",org.getOrgFullName());
                    }else{
                        map.put("org","");
                    }
                    Course course = userMajorCourseExam.getCourse();
                    if(course != null){
                        map.put("course",course.getCourseCode()+"_"+course.getCourseName());
                    }else{
                        map.put("course","");
                    }
                    map.put("semester","第"+semesterArr[Integer.valueOf(userMajorCourseExam.getSemester())]+"学期");
                    map.put("status",statusArr[userMajorCourseExam.getStatus()]);
                    xxList.add(map);
                }
            }

            //excesl列表头部
            String[] colNamesStrArr = new String[]{"学生","专业","站点","学期","课程","考试状态"};
            //数据库对应字段
            String[] colCodesStrArr = new String[]{"student","major","org","semester","course","status"};
            //文件名
            String title = "考试信息_".concat(CommonUtil.dateToString(new Date(),CommonUtil.ymd)).concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode());
            entity.commExport(response,title, xxList, Arrays.asList(colNamesStrArr), Arrays.asList(colCodesStrArr));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出数据失败");
        }
    }
}
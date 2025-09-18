package com.struggle.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.entity.ScoreRule;
import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.entity.UserLearning;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.base.param.UserLearningParam;
import com.struggle.common.base.service.CoursewareService;
import com.struggle.common.base.service.ScoreRuleService;
import com.struggle.common.base.service.UserCourseService;
import com.struggle.common.base.service.UserLearningService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.service.TenantService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 计算学习进度定时任务
 */
@Configuration
@ConditionalOnProperty(prefix = "scheduled", name = "learningTask", havingValue = "true")
public class LearningTask {

    @Resource
    private UserCourseService userCourseService;

    @Resource
    private CoursewareService coursewareService;

    @Resource
    private UserLearningService userLearningService;

    @Resource
    private TenantService tenantService;

    /**
     * 5分钟计算课程进度成绩
     */
    @Scheduled(cron="0 0/5 * * * ?")//五分钟
    public void initProgress() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -8);
        this.task(calendar);
    }

    /**
     * 每天计算课程进度成绩
     */
    @Scheduled(cron="0 0 4 * * ?") //4点钟执行
    public void everydayProgress() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        this.task(calendar);
    }

    private void task(Calendar calendar){
        Calendar nowcalendar = Calendar.getInstance();
        List<Tenant> list = tenantService.list(new LambdaQueryWrapper<Tenant>().ne(Tenant::getTenantId, 1));
        for(Tenant tenant:list){
            try {
                ThreadLocalContextHolder.setTenant(tenant.getTenantId());

                Long pageNum = 0L;
                Long pageSize = 500l;
                UserLearningParam param = new UserLearningParam();
                List<UserLearning> records = new ArrayList<>();

                QueryWrapper queryWrapper = new QueryWrapper<UserLearning>();
                queryWrapper.select("DISTINCT user_id,major_id,course_id");
                queryWrapper.gt("update_time", calendar.getTime());
                queryWrapper.lt("update_time",nowcalendar.getTime());

                do{
                    pageNum++;
                    param.setPage(pageNum);
                    param.setLimit(pageSize);
                    PageParam<UserLearning, UserLearningParam> page = new PageParam<>(param);
                    page.setDefaultOrder("course_id");

                    PageParam userLearningPage = userLearningService.page(page, queryWrapper);
                    records = userLearningPage.getRecords();
                    if(!CollectionUtils.isEmpty(records)) {
                        Set<Integer> courseIds = new HashSet<>();
                        Set<Integer> userIds = new HashSet<>();
                        Set<Integer> majorIds = new HashSet<>();
                        Map<String,UserLearning> userLearnMap = new HashMap<>();
                        for (UserLearning userLearning : records) {
                            courseIds.add(userLearning.getCourseId());
                            userIds.add(userLearning.getUserId());
                            majorIds.add(userLearning.getMajorId());
                            userLearnMap.put(userLearning.getUserId()+"_"+userLearning.getMajorId()+"_"+userLearning.getCourseId(),userLearning);
                        }

                        //没有考试，并且学习进度小于100分的
                        List<UserCourse> userCourseList = userCourseService.list(new LambdaQueryWrapper<UserCourse>()
                                .lt(UserCourse::getLearingProgress, 100)
                                .isNull(UserCourse::getExamScore)
                                .in(UserCourse::getUserId, userIds)
                                .in(UserCourse::getCourseId, courseIds)
                                .in(UserCourse::getMajorId, majorIds));

                        //计算进度
                        this.calculateProgress(userCourseList,userLearnMap);
                    }
                }while (records.size() == pageSize);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                ThreadLocalContextHolder.removeTenant();
            }
        }
    }


    private void calculateProgress(List<UserCourse> records,Map<String,UserLearning> userLearnMap){
        try{
            if(!CollectionUtils.isEmpty(records)){
                Set<Integer> courseIds = new HashSet<>();
                Set<Integer> userIds = new HashSet<>();
                Set<Integer> majorIds = new HashSet<>();

                //删掉多余的
                Iterator<UserCourse> iterator = records.iterator();
                while(iterator.hasNext()){
                    UserCourse userCourse = iterator.next();
                    UserLearning userLearning = userLearnMap.get(userCourse.getUserId() + "_" + userCourse.getMajorId() + "_" + userCourse.getCourseId());
                    if(userLearning == null){
                        iterator.remove();
                    }
                    courseIds.add(userCourse.getCourseId());
                    userIds.add(userCourse.getUserId());
                    majorIds.add(userCourse.getMajorId());
                }

                ThreadLocalContextHolder.setCloseTenant(true);
                //章节信息
                List<Courseware> courseList = coursewareService.list(
                        new LambdaQueryWrapper<Courseware>()
                                .select(Courseware::getCourseId,Courseware::getCoursewareId,Courseware::getDuration)
                                .in(Courseware::getCourseId, courseIds)
                                .isNotNull(Courseware::getDuration));
                ThreadLocalContextHolder.removeCloseTenant();
                Map<Integer,List<Courseware>> coursewareMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(courseList)){
                    for(Courseware courseware:courseList){
                        List<Courseware> coursewares = coursewareMap.get(courseware.getCourseId());
                        if(coursewares == null){
                            coursewares = new ArrayList<>();
                            coursewares.add(courseware);
                            coursewareMap.put(courseware.getCourseId(),coursewares);
                        }else{
                            coursewares.add(courseware);
                        }
                    }
                }

                //章节学习记录
                List<UserLearning> userLearningList = userLearningService.list(new LambdaQueryWrapper<UserLearning>()
                        .select(UserLearning::getUserId,UserLearning::getMajorId,UserLearning::getCourseId,UserLearning::getCoursewareId,UserLearning::getPlatProgress,UserLearning::getTotalPalyTime)
                        .in(UserLearning::getUserId, userIds).in(UserLearning::getMajorId, majorIds).in(UserLearning::getCourseId, courseIds));
                Map<String,UserLearning> userLearningMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(userLearningList)){
                    for(UserLearning userLearning:userLearningList){
                        userLearningMap.put(userLearning.getUserId()+"_"+userLearning.getMajorId()+"_"+userLearning.getCourseId()+"_"+userLearning.getCoursewareId(),userLearning);
                    }
                }

                for(UserCourse userCourse:records){
                    userCourse.setUpdateTime(null);
                    List<Courseware> coursewares = coursewareMap.get(userCourse.getCourseId());
                    int num = coursewares.size();
                    int progress = 0;
                    for(Courseware courseware:coursewares){
                        UserLearning userLearning = userLearningMap.get(userCourse.getUserId() + "_" + userCourse.getMajorId() + "_" + userCourse.getCourseId() + "_" + courseware.getCoursewareId());
                        if(userLearning != null){
                            progress+=userLearning.getPlatProgress();
                        }
                    }

                    int score = progress/num;
                    userCourse.setLearingProgress(score);
                    userCourse.setLearingScore(score);
                }
                userCourseService.updateBatchById(records,1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

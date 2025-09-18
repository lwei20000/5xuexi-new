package com.struggle.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.ScoreRule;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.base.service.ScoreRuleService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.param.TMajorCourseExamParam;
import com.struggle.common.exam.service.ITMajorCourseExamService;
import com.struggle.common.exam.service.ITUserMajorCourseExamService;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.service.TenantService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 考试定时任务
 */
@Configuration
@ConditionalOnProperty(prefix = "scheduled", name = "examTask", havingValue = "true")
public class ExamTask {

    @Resource
    private ITUserMajorCourseExamService tUserMajorCourseExamService;
    @Resource
    private ITMajorCourseExamService tMajorCourseExamService;
    @Resource
    private TenantService tenantService;
    @Resource
    private ScoreRuleService scoreRuleService;

    /**
     * 10分钟 考试过期后  修改保存状态的试卷为提交状态
     */
    @Scheduled(cron="0 0/10 * * * ?")//十分钟
    public void initProgress() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -10);
        this.task(calendar);
    }

    /**
     * 每天 考考试过期后  修改保存状态的试卷为提交状态
     */
    @Scheduled(cron="0 0 3 * * ?") //3点钟执行
    public void everydayProgress() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        this.task(calendar);
    }

    private void task(Calendar calendar){
        Calendar nowcalendar = Calendar.getInstance();
        List<Tenant> list = tenantService.list(new LambdaQueryWrapper<Tenant>().select(Tenant::getTenantId));
        for(Tenant tenant:list){
            try {
                ThreadLocalContextHolder.setTenant(tenant.getTenantId());
                List<ScoreRule> scoreRuleList = scoreRuleService.list();
                ScoreRule scoreRule = null;
                if(!CollectionUtils.isEmpty(scoreRuleList)) {
                     scoreRule = scoreRuleList.get(0);
                }

                /**
                 * 业务考试
                 */
                Long _pageNum = 0L;
                Long _pageSize = 100l;
                TMajorCourseExamParam _param = new TMajorCourseExamParam();
                List<TMajorCourseExam> _records = new ArrayList<>();

                QueryWrapper<TMajorCourseExam> _queryWrapper = new QueryWrapper<>();
                _queryWrapper.select("major_course_exam_id,major_course_id");
                _queryWrapper.gt("end_time",calendar.getTime());
                _queryWrapper.lt("end_time",nowcalendar.getTime());

                do{
                    _pageNum++;
                    _param.setPage(_pageNum);
                    _param.setLimit(_pageSize);
                    PageParam<TMajorCourseExam, TMajorCourseExamParam> _page = new PageParam<>(_param);

                    IPage<TMajorCourseExam> objectExamPage = tMajorCourseExamService.page(_page, _queryWrapper);
                    _records = objectExamPage.getRecords();
                    try {
                        tUserMajorCourseExamService.batchSubmitPaper(_records,scoreRule);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }while (_records.size() == _pageSize);

            }finally {
                ThreadLocalContextHolder.removeTenant();
            }
        }
    }
}

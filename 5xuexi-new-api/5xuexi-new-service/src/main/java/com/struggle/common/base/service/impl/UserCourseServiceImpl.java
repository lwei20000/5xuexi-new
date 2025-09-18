package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.*;
import com.struggle.common.base.mapper.UserCourseMapper;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.base.service.CourseService;
import com.struggle.common.base.service.MajorService;
import com.struggle.common.base.service.ScoreRuleService;
import com.struggle.common.base.service.UserCourseService;
import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.FileRecord;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.entity.UserTenant;
import com.struggle.common.system.service.OrgService;
import com.struggle.common.system.service.UserService;
import com.struggle.common.system.service.UserTenantService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse>  implements UserCourseService {

    @Resource
    private MajorService majorService;

    @Resource
    private UserService userService;

    @Resource
    private CourseService courseService;
    @Resource
    private OrgService orgService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private FileDfsUtil fileDfsUtil;
    @Resource
    private ScoreRuleService scoreRuleService;

    @Override
    public PageResult<UserCourse> pageRel(UserCourseParam param) {
        // 分页查询
        PageParam<UserCourse, UserCourseParam> page = new PageParam<>(param);
        page.setDefaultOrder("m.major_year desc,a.user_id,a.major_id,c.semester desc");
        List<UserCourse> list = baseMapper.selectPageRel(page, param);
        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public void dataExport(HttpServletResponse response, UserCourseParam param) {
        try {
            ExcelOperation entity = new ExcelOperation(ExcelConstant.EXCEL.COLLEGE_EXCEL_EXPORT_DATA.getCode());
            List<UserCourse> userCourses = this.listRel(param,true);
            List<Map<String, Object>>  xxList = new ArrayList<>();
            String[] semesterArr = new String[]{"","一","二","三","四","五","六","七","八","九","十"};
            if (!CollectionUtils.isEmpty(userCourses)) {
                for (UserCourse userCourse : userCourses) {
                    Map<String, Object> map = new HashMap<>();
                    User user = userCourse.getUser();
                    if(user !=null){
                        map.put("student",user.getRealname() +"_"+ user.getUsername());
                    }else{
                        map.put("student","");
                    }

                    // 学号
                    String userNumber = userCourse.getUserNumber();
                    if(StringUtils.hasText(userNumber)){
                        map.put("userNumber",userNumber);
                    }else{
                        map.put("userNumber","");
                    }

                    Major major = userCourse.getMajor();
                    if(major != null){
                        map.put("major",major.getMajorYear()+"_"+major.getMajorCode()+"_"+major.getMajorName()+"_"+major.getMajorType()+
                                 "_"+major.getMajorGradation()+"_"+major.getMajorForms()+"_"+major.getMajorLength()+"年制");
                    }else{
                        map.put("major","");
                    }
                    Org org = userCourse.getOrg();
                    if(org != null){
                        map.put("org",org.getOrgFullName());
                    }else{
                        map.put("org","");
                    }
                    Course course = userCourse.getCourse();
                    if(course != null){
                        map.put("course",course.getCourseCode()+"_"+course.getCourseName());
                    }else{
                        map.put("course","");
                    }
                    map.put("semester","第"+semesterArr[Integer.valueOf(userCourse.getSemester())]+"学期");
                    map.put("learingProgress",userCourse.getLearingProgress());
                    map.put("learingScore", userCourse.getLearingScore());
                    map.put("examScore", userCourse.getExamScore());
                    map.put("totalScore", userCourse.getTotalScore());
                    xxList.add(map);
                }
            }

            //excesl列表头部
            String[] colNamesStrArr = new String[]{"学生","学号","专业","站点","学期","课程","学习进度","学习成绩","考试成绩","总成绩"};
            //数据库对应字段
            String[] colCodesStrArr = new String[]{"student","userNumber","major","org","semester","course","learingProgress","learingScore","examScore","totalScore"};
            //文件名
            String title = "学习进度信息_".concat(CommonUtil.dateToString(new Date(),CommonUtil.ymd)).concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode());
            entity.commExport(response,title, xxList, Arrays.asList(colNamesStrArr), Arrays.asList(colCodesStrArr));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出数据失败");
        }
    }

    @Override
    public void updateScore(UserCourseParam param){
        List<UserCourse> list = baseMapper.selectListRel(param);
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> userCourseids = new ArrayList<>();
            for(UserCourse userCourse:list){
                if(userCourse.getLearingProgress()<100 && userCourse.getExamScore() == null){
                    userCourseids.add(userCourse.getId());
                }
            }
            if(!CollectionUtils.isEmpty(userCourseids)){
                baseMapper.update(null,new LambdaUpdateWrapper<UserCourse>()
                        .set(UserCourse::getLearingScore,100)
                        .set(UserCourse::getLearingProgress,100)
                        .in(UserCourse::getId,userCourseids));
            }
        }
    }

    @Override
    public void updateExamScore(UserCourse param,List<Integer> orgIds) {
        UserCourseParam _param = new UserCourseParam();
        _param.setId(param.getId());
        _param.setOrgIds(orgIds);
        List<UserCourse> userCourses = baseMapper.selectListRel(_param);
        if(!CollectionUtils.isEmpty(userCourses)){
            //修改考试成绩
            if(param.getExamScore() == -1 ){
                baseMapper.update(null,new LambdaUpdateWrapper<UserCourse>()
                                .set(UserCourse::getLearingScore,param.getLearingScore())
                                .set(UserCourse::getLearingProgress,param.getLearingScore())
                                .set(UserCourse::getExamScore,null)
                                .set(UserCourse::getTotalScore,null)
                                .eq(UserCourse::getId,param.getId()));
            }else{
                List<ScoreRule> scoreRuleList = scoreRuleService.list();
                int learningScoreProportions = 40;
                int examScoreProportions = 60;
                if(!CollectionUtils.isEmpty(scoreRuleList)) {
                    ScoreRule scoreRule = scoreRuleList.get(0);
                    learningScoreProportions = scoreRule.getLearningScoreProportions();
                    examScoreProportions = scoreRule.getExamScoreProportions();
                }

                int _totalScore = 0;
                //计算总成绩
                float l_score = param.getLearingScore()*(learningScoreProportions/100.0f);
                float k_score = param.getExamScore()*(examScoreProportions/100.0f);
                if((l_score+k_score)>=100){
                    _totalScore  = 100;
                }else{
                    _totalScore = (int)(l_score+k_score);
                }

                baseMapper.update(null,new LambdaUpdateWrapper<UserCourse>()
                        .set(UserCourse::getLearingScore,param.getLearingScore())
                        .set(UserCourse::getLearingProgress,param.getLearingScore())
                        .set(UserCourse::getExamScore,param.getExamScore())
                        .set(UserCourse::getTotalScore,_totalScore)
                        .eq(UserCourse::getId,param.getId()));
            }
        }
    }

    @Override
    public ApiResult<?> scoreImportBatch(MultipartFile file,UserCourseParam param, HttpServletRequest request, HttpServletResponse res) {
        try {

            int sheetIndex = 0;
            ExcelOperation excellReader = new ExcelOperation(file.getInputStream());
            XSSFWorkbook wb = excellReader.getWb();
            DataFormat dataFormat = wb.createDataFormat();// 设置可编辑列为文本格式,打开excel编辑不会自动科学计数法
            CellStyle style = wb.createCellStyle();
            style.setDataFormat(dataFormat.getFormat("@"));
            style.setAlignment(HorizontalAlignment.CENTER);//水平居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            style.setWrapText(true);

            CellStyle style_1 = wb.createCellStyle();
            style_1.cloneStyleFrom(style);
            style_1.setBorderBottom(BorderStyle.THIN);
            style_1.setBorderLeft(BorderStyle.THIN);
            style_1.setBorderRight(BorderStyle.THIN);
            style_1.setBorderTop(BorderStyle.THIN);

            List<String[]> list = excellReader.readAllExcelContent(sheetIndex);
            if (list.size() <= 3) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "模板没有数据,请重新导入");
            if(list.size()>5003) return new ApiResult<>(Constants.RESULT_ERROR_CODE, "一次只支持导入5000,请重新导入");

            Set<String> majorYearList = new HashSet<>();
            List<String> userList = new ArrayList<>();
            Set<String> courseCodeList = new HashSet<>();
            Set<String> courseNameList = new HashSet<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    userList.add(item[0]);
                }
                if(StringUtils.hasText(item[1])){
                    majorYearList.add(item[1]);
                }
                if(StringUtils.hasText(item[2])){
                    String[] s = item[2].split("_");
                    if(s.length == 2){
                        courseCodeList.add(s[0]);
                        courseNameList.add(s[1]);
                    }
                }
            }

            // 用户名/身份证_用户
            Map<String,User> usernameMap = new HashMap<>();
            Map<String,User> idCardMap = new HashMap<>();
            Map<Integer, UserTenant> userTenantMap = new HashMap<>();
            List<Integer> userIds = new ArrayList<>();
            if(!CollectionUtils.isEmpty(userList)){
                List<User> usernameExists = userService.list(new LambdaQueryWrapper<User>()
                        .select(User::getUserId,User::getIdCard,User::getUsername)
                        .in(User::getUsername,userList).or().in(User::getIdCard,userList));
                if(!CollectionUtils.isEmpty(usernameExists)){
                    for(User user:usernameExists){
                        usernameMap.put(user.getUsername(),user);
                        idCardMap.put(user.getIdCard(),user);
                        userIds.add(user.getUserId());
                    }

                    List<UserTenant> userTenants = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getUserId, userIds));
                    if(!CollectionUtils.isEmpty(userTenants)){
                        for (UserTenant userTenant:userTenants){
                            userTenantMap.put(userTenant.getUserId(),userTenant);
                        }
                    }
                }
            }

            Map<String,Course> courseMap = new HashMap<>();
            Set<Integer> courseIds = new HashSet<>();
            if(!CollectionUtils.isEmpty(courseCodeList) && !CollectionUtils.isEmpty(courseNameList) ){
                CourseParam courseParam = new CourseParam();
                courseParam.setCourseNameList(new ArrayList<>(courseNameList));
                courseParam.setCourseCodeList(new ArrayList<>(courseCodeList));
                List<Course> list1 = courseService.listRel(courseParam);
                if(!CollectionUtils.isEmpty(list1)){
                    for(Course course:list1){
                        courseMap.put(course.getCourseCode()+"_"+course.getCourseName(), course);
                        courseIds.add(course.getCourseId());
                    }
                }
            }


            //用户课程
            Map<String,List<UserCourse>> hasUserCourseMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(userIds) && !CollectionUtils.isEmpty(majorYearList) && !CollectionUtils.isEmpty(courseIds)){
                param.setUserIds(userIds);
                param.setMajorYears(new ArrayList<>(majorYearList));
                param.setCourseIds(new ArrayList<>(courseIds));
                List<UserCourse> userCourses = this.baseMapper.selectListRel(param);
                if(!CollectionUtils.isEmpty(userCourses)){
                    for (UserCourse userCourse:userCourses){
                        String key = userCourse.getUserId()+"_"+userCourse.getMajorYear()+"_"+userCourse.getCourseId();
                        List<UserCourse> userCourses1 = hasUserCourseMap.get(key);
                        if(CollectionUtils.isEmpty(userCourses1)){
                            userCourses1 = new ArrayList<>();
                            userCourses1.add(userCourse);
                            hasUserCourseMap.put(key,userCourses1);
                        }else{
                            userCourses1.add(userCourse);
                        }
                    }
                }
            }

            //更新课表成绩
            List<ScoreRule> scoreRuleList = scoreRuleService.list();
             int learningScoreProportions = 40;
             int examScoreProportions = 60;
            if(!CollectionUtils.isEmpty(scoreRuleList)) {
                ScoreRule scoreRule = scoreRuleList.get(0);
                learningScoreProportions = scoreRule.getLearningScoreProportions();
                examScoreProportions = scoreRule.getExamScoreProportions();
            }

            int lastIndex = list.get(2).length;
            Map<String,UserCourse> userCourseMap = new HashMap<>();
            List<UserCourse> rowList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                Integer userId =null;
                Integer courseId =null;
                if(!StringUtils.hasText(arr[0])){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "登录账号/身份证号不能为空", style);
                    continue;
                }else{
                    User _user = usernameMap.get(arr[0]);
                    if(_user == null){
                        _user = idCardMap.get(arr[0]);
                    }
                    if(_user != null){
                        UserTenant userTenant = userTenantMap.get(_user.getUserId());
                        if(userTenant != null){
                            userId = _user.getUserId();
                        }else{
                            //当前租户没有
                            excellReader.setCellValue(sheetIndex, i, lastIndex, "根据登录账号/身份证号没有找到用户", style);
                            continue;
                        }
                    }else{
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "根据登录账号/身份证号没有找到用户", style);
                        continue;
                    }
                }

                if(!StringUtils.hasText(arr[1])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "招生年份不能为空", style);
                    continue;
                }

                if(!StringUtils.hasText(arr[2])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "课程不能为空", style);
                    continue;
                }else{
                    Course course = courseMap.get(arr[2]);
                    if(course == null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "课程未找到", style);
                        continue;
                    }
                    courseId = course.getCourseId();
                }

                if(!StringUtils.hasText(arr[3])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "考试成绩不能为空", style);
                    continue;
                }

                if(!CommonUtil.isInt(arr[3])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "考试成绩只能是大于0的整数", style);
                    continue;
                }

                if(Integer.valueOf(arr[3]) > 100){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "考试成绩只能小于等于100", style);
                    continue;
                }

                String key = userId + "_" + arr[1]+"_"+courseId;
                List<UserCourse> userCourseLists = hasUserCourseMap.get(key);
                if(CollectionUtils.isEmpty(userCourseLists)){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-招生年份不存在课程", style);
                    continue;
                }

                if(userCourseLists.size()>1){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-招生年份下找到二个课程", style);
                    continue;
                }

                UserCourse _userCourse = userCourseLists.get(0);

                UserCourse userCourse = new UserCourse();
                userCourse.setId(_userCourse.getId());
                userCourse.setExamScore(Integer.valueOf(arr[3]));

                int _totalScore = 0;
                //计算总成绩
                float l_score = _userCourse.getLearingScore()*(learningScoreProportions/100.0f);
                float k_score = userCourse.getExamScore()*(examScoreProportions/100.0f);
                if((l_score+k_score)>=100){
                    _totalScore  = 100;
                }else{
                    _totalScore = (int)(l_score+k_score);
                }
                userCourse.setTotalScore(_totalScore);

                UserCourse userCourse1 = userCourseMap.get(key);
                if(userCourse1 != null){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-考试成绩导入重复", style);
                    continue;
                }else{
                    userCourseMap.put(key,userCourse);
                }

                excellReader.removeRow(0,i);
                rowList.add(userCourse);
            }

            if (!rowList.isEmpty()) {
                this.updateBatchById(rowList,5000);
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("用户论文成绩导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
                // TODO 上传错误文件
                return new ApiResult<>(999, "导入成功:" + rowList.size() + "条，失败：" + num + "条，失败原因详情请查看Excel。", upload.getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
        return new ApiResult<>(Constants.RESULT_OK_CODE, Constants.RESULT_OK_MSG);
    }

    @Override
    public void scoreTemplateExport(HttpServletResponse response) {
        try {
            ExcelOperation entity = new ExcelOperation("/user_course_score_template.xlsx");

            List<Course> courseList = courseService.listRel(new CourseParam());
            if(!CollectionUtils.isEmpty(courseList)){
                String[] courseArr = new String[courseList.size()];
                int index = 0;
                for (Course course : courseList) {
                    courseArr[index] = course.getCourseCode()+"_"+course.getCourseName();
                    index++;
                }
                entity.generateRangeList(2, courseArr,3,null,true,0);
            }

            entity.print(response, "专业课程导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    @Override
    public List<UserCourse> listRel(UserCourseParam param,boolean detail) {
        PageParam<UserCourse, UserCourseParam> page = new PageParam<>(param);
        page.setDefaultOrder("major_year desc,user_id,major_id,semester desc");
        List<UserCourse> list = baseMapper.selectListRel(param);
        if(detail){
            this.initOther(list);
        }else{
            this.initOther2(list);
        }
        return page.sortRecords(list);
    }

    private void initOther(List<UserCourse> userCourses){
        if(!CollectionUtils.isEmpty(userCourses)){
            Set<Integer> courseIds = new HashSet<>();
            Set<Integer> userIds = new HashSet<>();
            Set<Integer> majorIds = new HashSet<>();
            Set<Integer> orgIds = new HashSet<>();
            for (UserCourse userCourse : userCourses) {
                courseIds.add(userCourse.getCourseId());
                userIds.add(userCourse.getUserId());
                majorIds.add(userCourse.getMajorId());
                if(userCourse.getOrgId() !=null){
                    orgIds.add(userCourse.getOrgId());
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

            for (UserCourse userCourse : userCourses) {
                userCourse.setMajor(majorMap.get(userCourse.getMajorId()));
                userCourse.setCourse(courseMap.get(userCourse.getCourseId()));
                User user = userMap.get(userCourse.getUserId());
                if(user!=null){
                    user.setPassword(null);
                    userCourse.setUser(user);
                }
                if(userCourse.getOrgId() !=null){
                    userCourse.setOrg(orgMap.get(userCourse.getOrgId()));
                }
            }
        }
    }
    private void initOther2(List<UserCourse> userCourses){
        if(!CollectionUtils.isEmpty(userCourses)){
            Set<Integer> courseIds = new HashSet<>();
            for (UserCourse userCourse : userCourses) {
                courseIds.add(userCourse.getCourseId());
            }

            CourseParam c = new CourseParam();
            c.setCourseIds(new ArrayList<>(courseIds));
            List<Course> courses = courseService.listRel(c);
            Map<Integer, Course> courseMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(courses)) {
                courseMap = courses.stream().collect(Collectors.toMap(Course::getCourseId, Course -> Course));
            }

            for (UserCourse userCourse : userCourses) {
                userCourse.setCourse(courseMap.get(userCourse.getCourseId()));
            }
        }
    }
}

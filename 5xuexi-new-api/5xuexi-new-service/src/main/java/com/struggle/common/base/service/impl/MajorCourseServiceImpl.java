package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.*;
import com.struggle.common.base.mapper.MajorCourseMapper;
import com.struggle.common.base.mapper.UserMajorMapper;
import com.struggle.common.base.param.CopyMajorCourseParam;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.base.service.*;
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
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.entity.FileRecord;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.service.DictionaryDataService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 专业与课程关系Service实现
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Service
public class MajorCourseServiceImpl extends ServiceImpl<MajorCourseMapper, MajorCourse> implements MajorCourseService {

   @Resource
   private MajorService majorService;

    @Resource
    private CourseService courseService;

    @Resource
    private DictionaryDataService dictionaryDataService;

    @Resource
    private FileDfsUtil fileDfsUtil;

    @Resource
    private UserMajorMapper userMajorMapper;

    @Resource
    private UserCourseService userCourseService;

    @Resource
    private UserLearningService userLearningService;

    @Override
    public PageResult<MajorCourse> pageRel(MajorCourseParam param) {
        PageParam<MajorCourse, MajorCourseParam> page = new PageParam<MajorCourse, MajorCourseParam>(param);
        page.setDefaultOrder("b.major_year desc,b.major_id,a.semester desc"); //desc只能小写
        List<MajorCourse> list = baseMapper.selectPageRel(page, param);
        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<MajorCourse> listRel(MajorCourseParam param) {
        return baseMapper.selectListRel(param);
    }

    @Override
    @Transactional
    public boolean saveMajorCourse(MajorCourse majorCourse) {

        if (this.baseMapper.selectCount(new LambdaQueryWrapper<MajorCourse>()
                .eq(MajorCourse::getMajorId, majorCourse.getMajorId())
                .eq(MajorCourse::getCourseId, majorCourse.getCourseId())) > 0) {
            throw new BusinessException("专业、课程唯一，已存在");
        }

        this.baseMapper.insert(majorCourse);
        List<MajorCourse> majorCourseList = new ArrayList<>();
        majorCourseList.add(majorCourse);

        this.addUserCourse(majorCourseList);

        return true;
    }

    @Override
    @Transactional
    public boolean updateMajorCourse(MajorCourse majorCourse) {
        MajorCourse byId = this.baseMapper.selectById(majorCourse.getId());
        if(byId == null){
            throw new BusinessException("专业与课程，未找到");
        }

        if (this.baseMapper.selectCount(new LambdaQueryWrapper<MajorCourse>()
                .eq(MajorCourse::getMajorId, majorCourse.getMajorId())
                .eq(MajorCourse::getCourseId, majorCourse.getCourseId())
                .ne(MajorCourse::getId, majorCourse.getId())) > 0) {
            throw new BusinessException("专业、课程唯一，已存在");
        }

        majorCourse.setUpdateTime(null);
        this.baseMapper.updateById(majorCourse);
        if(!byId.getMajorId().equals(majorCourse.getMajorId()) || !byId.getCourseId().equals(majorCourse.getCourseId())){
            //删除原来的专业课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getMajorId,byId.getMajorId()).eq(UserCourse::getCourseId,byId.getCourseId()));
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getMajorId, byId.getMajorId()).eq(UserLearning::getCourseId,byId.getCourseId()));

            List<MajorCourse> majorCourseList = new ArrayList<>();
            majorCourseList.add(majorCourse);
            this.addUserCourse(majorCourseList);
        }
        return true;
    }

    @Override
    public MajorCourse getByIdRel(Integer id) {
        MajorCourseParam param = new MajorCourseParam();
        param.setId(id);
        return param.getOne(baseMapper.selectListRel(param));
    }

    @Override
    @Transactional
    public boolean copy(CopyMajorCourseParam majorCourse) {
        if(majorCourse == null){
            throw new BusinessException("COPY对象不能为空");
        }
        if(!StringUtils.hasText(majorCourse.getMajorYear()) && CollectionUtils.isEmpty(majorCourse.getMajorIds()) ){
            throw new BusinessException("来源年份和来源专业至少填一个");
        }
        if(!StringUtils.hasText(majorCourse.getSemester())){
            throw new BusinessException("来源学期不能为空");
        }
        if(!StringUtils.hasText(majorCourse.getTargetMajorYear())){
            throw new BusinessException("目标年份不能为空");
        }

        //来源专业
        List<Major> majors = null;
        if(!CollectionUtils.isEmpty(majorCourse.getMajorIds())){
            majors = majorService.listByIds(majorCourse.getMajorIds());
        }else{
            majors = majorService.list(Wrappers.<Major>lambdaQuery().eq(Major::getMajorYear,majorCourse.getMajorYear()));
        }

        if(CollectionUtils.isEmpty(majors)){
            throw new BusinessException("来源专业未找到");
        }

        Set<String> majorNameList = new HashSet<>();
        Set<String> majorTypeList = new HashSet<>();
        Set<String> majorGradationList = new HashSet<>();
        Set<String> majorFormsList = new HashSet<>();
        Set<String> majorCodeList = new HashSet<>();
        Set<String> majorLengthList = new HashSet<>();
        Map<String,Major> majorMap = new HashMap<>();
        for(Major _major:majors){
            majorNameList.add(_major.getMajorName());
            majorTypeList.add(_major.getMajorType());
            majorGradationList.add(_major.getMajorGradation());
            majorFormsList.add(_major.getMajorForms());
            majorLengthList.add(_major.getMajorLength());
            majorCodeList.add(_major.getMajorCode());
            String key  = majorCourse.getTargetMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                    "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                    "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
            majorMap.put(key,_major);
        }

        //目标专业
        List<Major> _majors = majorService.list(Wrappers.<Major>lambdaQuery().eq(Major::getMajorYear, majorCourse.getTargetMajorYear())
                .in(Major::getMajorName, majorNameList)
                .in(Major::getMajorCode, majorCodeList)
                .in(Major::getMajorType, majorTypeList)
                .in(Major::getMajorGradation, majorGradationList)
                .in(Major::getMajorForms, majorFormsList)
                .in(Major::getMajorLength, majorLengthList));

        if(CollectionUtils.isEmpty(_majors)){
            throw new BusinessException("目标专业未找到");
        }

        Map<Integer,Integer> majorIdMap = new HashMap<>();
        for(Major _major:_majors){
            String key  = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                    "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                    "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
            Major major = majorMap.get(key);
            if(major != null){
                majorIdMap.put(major.getMajorId(),_major.getMajorId());
            }
        }

        //来源专业课程
        List<Integer> majorIds = majors.stream().map(Major::getMajorId).collect(Collectors.toList());
        List<MajorCourse> majorCourses = this.baseMapper.selectList(Wrappers.<MajorCourse>lambdaQuery()
                .eq(MajorCourse::getSemester, majorCourse.getSemester())
                .in(MajorCourse::getMajorId, majorIds));

        if(CollectionUtils.isEmpty(majorCourses)){
            throw new BusinessException("来源专业课程未找到");
        }

        //目标专业课程
        List<Integer> _majorIds = _majors.stream().map(Major::getMajorId).collect(Collectors.toList());
        List<MajorCourse> _majorCourses = this.baseMapper.selectList(Wrappers.<MajorCourse>lambdaQuery()
                .in(MajorCourse::getMajorId, _majorIds));
        Map<String,MajorCourse> majorCourseMap = new HashMap<>();
        for (MajorCourse _majorCourse:_majorCourses){
            majorCourseMap.put(_majorCourse.getMajorId()+"_"+_majorCourse.getCourseId(),_majorCourse);
        }

        List<MajorCourse> majorCourseList = new ArrayList<>();
        for (MajorCourse _majorCourse:majorCourses){
            Integer majorId = majorIdMap.get(_majorCourse.getMajorId());
            if(majorId != null){
                MajorCourse majorCourse1 = majorCourseMap.get(majorId + "_" + _majorCourse.getCourseId());
                if(majorCourse1 == null){
                    majorCourse1 = new MajorCourse();
                    majorCourse1.setMajorId(majorId);
                    majorCourse1.setCourseId(_majorCourse.getCourseId());
                    majorCourse1.setSemester(_majorCourse.getSemester());
                    majorCourseList.add(majorCourse1);
                }
            }
        }

        this.saveBatch(majorCourseList,5000);
        this.addUserCourse(majorCourseList);
        return true;
    }

    @Override
    @Transactional
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res) {
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
            Set<String> majorCodeList = new HashSet<>();
            Set<String> majorNameList = new HashSet<>();
            Set<String> majorTypeList = new HashSet<>();
            Set<String> majorGradationList = new HashSet<>();
            Set<String> majorFormsList = new HashSet<>();
            Set<String> majorLengthList = new HashSet<>();

            Set<String> courseCodeList = new HashSet<>();
            Set<String> courseNameList = new HashSet<>();
            Set<String> semesterList = new HashSet<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    String[] s = item[0].split("_");
                    if(s.length == 7){
                        majorYearList.add(s[0]);
                        majorCodeList.add(s[1]);
                        majorNameList.add(s[2]);
                        majorTypeList.add(s[3]);
                        majorGradationList.add(s[4]);
                        majorFormsList.add(s[5]);
                        majorLengthList.add(s[6]);
                    }
                }
                if(StringUtils.hasText(item[1])){
                    semesterList.add(item[1]);
                }
                if(StringUtils.hasText(item[2])){
                    String[] s = item[2].split("_");
                    if(s.length == 2){
                        courseCodeList.add(s[0]);
                        courseNameList.add(s[1]);
                    }
                }
            }
            // KEY_专业
            Map<String, Major> majorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(majorYearList) && !CollectionUtils.isEmpty(majorCodeList) && !CollectionUtils.isEmpty(majorNameList)&& !CollectionUtils.isEmpty(majorTypeList)
                    && !CollectionUtils.isEmpty(majorGradationList)&& !CollectionUtils.isEmpty(majorFormsList)&& !CollectionUtils.isEmpty(majorLengthList)){
                List<Major> majorExists = majorService.list(new LambdaQueryWrapper<Major>()
                        .in(Major::getMajorYear,majorYearList)
                        .in(Major::getMajorCode, majorCodeList)
                        .in(Major::getMajorName, majorNameList)
                        .in(Major::getMajorType, majorTypeList)
                        .in(Major::getMajorGradation, majorGradationList)
                        .in(Major::getMajorForms, majorFormsList)
                        .in(Major::getMajorLength, majorLengthList));
                if(!CollectionUtils.isEmpty(majorExists)){
                    for(Major _major:majorExists){
                        String key  = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                                "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                                "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
                        majorMap.put(key,_major);
                    }
                }
            }

            Map<String,Course> courseMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(courseCodeList) && !CollectionUtils.isEmpty(courseNameList) ){
                CourseParam param = new CourseParam();
                param.setCourseNameList(new ArrayList<>(courseNameList));
                param.setCourseCodeList(new ArrayList<>(courseCodeList));
                List<Course> list1 = courseService.listRel(param);
                if(!CollectionUtils.isEmpty(list1)){
                    for(Course course:list1){
                        courseMap.put(course.getCourseCode()+"_"+course.getCourseName(),course);
                    }
                }
            }

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("semester");
            List<DictionaryData> semesterDatas = dictionaryDataService.listRel(param);
            Map<String,String> semesterMap = new HashMap<>();
            for (DictionaryData dictionaryData:semesterDatas){
                semesterMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }

            int lastIndex = list.get(2).length;
            List<MajorCourse> rowList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                MajorCourse majorCourse = new MajorCourse();
                if(!StringUtils.hasText(arr[0])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业不能为空", style);
                    continue;
                }else{
                    Major major = majorMap.get(arr[0]);
                    if(major == null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "专业未找到", style);
                        continue;
                    }
                    majorCourse.setMajorId(major.getMajorId());
                }

                if(!StringUtils.hasText(arr[1])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "学期不能为空", style);
                    continue;
                }else{
                    String s = semesterMap.get(arr[1]);
                    if(!StringUtils.hasText(s)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "学期错误，未找到对应字典", style);
                        continue;
                    }
                    majorCourse.setSemester(s);
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
                    majorCourse.setCourseId(course.getCourseId());
                }

                excellReader.removeRow(0,i);
                CommonUtil.changeToNull(majorCourse);
                rowList.add(majorCourse);
            }

            if (!rowList.isEmpty()) {
                this.saveBatch(rowList,5000);
                this.addUserCourse(rowList);
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("专业课程导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
                return new ApiResult<>(999, "导入成功:" + rowList.size() + "条，失败：" + num + "条，失败原因详情请查看Excel。", upload.getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
        return new ApiResult<>(Constants.RESULT_OK_CODE, Constants.RESULT_OK_MSG);
    }

    @Override
    public void templateExport(HttpServletResponse res) {
        try {
            ExcelOperation entity = new ExcelOperation("/major_course_template.xlsx");

            List<Major> list = majorService.list(new LambdaQueryWrapper<Major>().orderByDesc(Major::getMajorYear));
            if(!CollectionUtils.isEmpty(list)){
                String[] majorArr = new String[list.size()];
                int index = 0;
                for (Major _major : list) {
                    majorArr[index] = _major.getMajorYear()+"_"+_major.getMajorCode()+"_"+_major.getMajorName()+
                            "_"+_major.getMajorType()+"_"+_major.getMajorGradation()+
                            "_"+_major.getMajorForms()+"_"+_major.getMajorLength();
                    index++;
                }
                entity.generateRangeList(0, majorArr,3,null,true,0);
            }

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("semester");
            List<DictionaryData> semesterDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(semesterDatas)){
                String[] semesterArr = new String[semesterDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : semesterDatas) {
                    semesterArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(1, semesterArr,3,null,false,0);
            }

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

            entity.print(res, "专业课程导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    private void addUserCourse(List<MajorCourse> majorCourses){
        if(!CollectionUtils.isEmpty(majorCourses)){
            Set<Integer> majorIds =new HashSet<>();
            Set<Integer> courseIds =new HashSet<>();
            Map<Integer,List<MajorCourse>> majorCourseMap =new HashMap<>();
            for(MajorCourse majorCourse:majorCourses){
                majorIds.add(majorCourse.getMajorId());
                courseIds.add(majorCourse.getCourseId());
                List<MajorCourse> majorCourses1 = majorCourseMap.get(majorCourse.getMajorId());
                if(majorCourses1 == null){
                    majorCourses1 = new ArrayList<>();
                    majorCourses1.add(majorCourse);
                    majorCourseMap.put(majorCourse.getMajorId(),majorCourses1);
                }else{
                    majorCourses1.add(majorCourse);
                }
            }
            List<UserMajor> list = userMajorMapper.selectList(new LambdaQueryWrapper<UserMajor>().in(UserMajor::getMajorId, majorIds));
            if(!CollectionUtils.isEmpty(list)){
                List<UserCourse> userCourseList = userCourseService.list(new LambdaQueryWrapper<UserCourse>().in(UserCourse::getMajorId, majorIds).in(UserCourse::getCourseId,courseIds));
                Map<String,UserCourse> userCourseMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(userCourseList)){
                    for(UserCourse userCourse:userCourseList){
                        userCourseMap.put(userCourse.getUserId()+"_"+userCourse.getMajorId()+"_"+userCourse.getCourseId(),userCourse);
                    }
                }
                List<UserCourse> _userCourseList = new ArrayList<>();
                for(UserMajor userMajor:list){
                    List<MajorCourse> majorCourses1 = majorCourseMap.get(userMajor.getMajorId());
                    if(!CollectionUtils.isEmpty(majorCourses1)){
                        for(MajorCourse majorCourse:majorCourses1){
                            UserCourse userCourse1 = userCourseMap.get(userMajor.getUserId() + "_" + majorCourse.getMajorId() + "_" + majorCourse.getCourseId());
                            if(userCourse1 == null){
                                UserCourse userCourse = new UserCourse();
                                userCourse.setCourseId(majorCourse.getCourseId());
                                userCourse.setUserId(userMajor.getUserId());
                                userCourse.setMajorId(userMajor.getMajorId());
                                _userCourseList.add(userCourse);
                            }
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(_userCourseList)){
                    userCourseService.saveBatch(_userCourseList,5000);
                }
            }
        }
    }

    private void initOther(List<MajorCourse> majorCourses){
        if(!CollectionUtils.isEmpty(majorCourses)){
            Set<Integer> courseIds = new HashSet<>();
            Set<Integer> majorIds = new HashSet<>();
            for (MajorCourse majorCourse : majorCourses) {
                courseIds.add(majorCourse.getCourseId());
                majorIds.add(majorCourse.getMajorId());
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
            for (MajorCourse majorCourse : majorCourses) {
                majorCourse.setMajor(majorMap.get(majorCourse.getMajorId()));
                majorCourse.setCourse(courseMap.get(majorCourse.getCourseId()));
            }
        }
    }
}

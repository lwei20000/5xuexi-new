package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.*;
import com.struggle.common.base.mapper.UserMajorMapper;
import com.struggle.common.base.param.UserMajorParam;
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
import com.struggle.common.core.world.ImgObject;
import com.struggle.common.core.world.WordOperation;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.service.*;
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
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户与专业关系Service实现
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Service
public class UserMajorServiceImpl extends ServiceImpl<UserMajorMapper, UserMajor> implements UserMajorService {

    @Resource
    private MajorService majorService;
    @Resource
    private UserService userService;
    @Resource
    private OrgService orgService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private FileDfsUtil fileDfsUtil;
    @Resource
    private MajorCourseService majorCourseService;
    @Resource
    private UserCourseService userCourseService;
    @Resource
    private UserLearningService userLearningService;
    @Resource
    private TenantService tenantService;

    @Override
    public PageResult<UserMajor> pageRel(UserMajorParam param) {
        // 分页查询
        PageParam<UserMajor, UserMajorParam> page = new PageParam<>(param);
        page.setDefaultOrder("m.major_year desc,a.user_id,a.major_id");
        List<UserMajor> list = baseMapper.selectPageRel(page, param);
        this.initOther(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<UserMajor> listRel(UserMajorParam param) {
        List<UserMajor> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<UserMajor, UserMajorParam> page = new PageParam<>();
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    @Override
    @Transactional
    public boolean saveUserMajor(UserMajor studentMajor) {

        if (baseMapper.selectCount(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getMajorId, studentMajor.getMajorId())
                .eq(UserMajor::getUserId, studentMajor.getUserId())) > 0) {
            throw new BusinessException("用户、专业唯一，已存在");
        }

        if(StringUtils.hasText(studentMajor.getUserNumber())){
            if (baseMapper.selectCount(new LambdaQueryWrapper<UserMajor>()
                    .eq(UserMajor::getUserNumber, studentMajor.getUserNumber())
                    .ne(UserMajor::getUserId, studentMajor.getUserId())) > 0) {
                throw new BusinessException("学号唯一重复");
            }
        }
        baseMapper.insert(studentMajor);

        List<UserMajor> userMajors = new ArrayList<>();
        userMajors.add(studentMajor);
        this.addUserCourse(userMajors);

        return true;
    }

    @Override
    @Transactional
    public boolean updateUserMajor(UserMajor studentMajor) {
        UserMajor byId = this.baseMapper.selectById(studentMajor.getId());
        if(byId == null){
            throw new BusinessException("数据未找到");
        }

        if (this.baseMapper.selectCount(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getMajorId, studentMajor.getMajorId())
                .eq(UserMajor::getUserId, studentMajor.getUserId())
                .ne(UserMajor::getId, studentMajor.getId())) > 0) {
            throw new BusinessException("用户、专业唯一，已存在");
        }

        if(StringUtils.hasText(studentMajor.getUserNumber())){
            if (this.baseMapper.selectCount(new LambdaQueryWrapper<UserMajor>()
                    .eq(UserMajor::getUserNumber, studentMajor.getUserNumber())
                    .ne(UserMajor::getUserId, studentMajor.getUserId())) > 0) {
                throw new BusinessException("学号唯一重复");
            }
        }

        studentMajor.setUpdateTime(null);
        this.baseMapper.update(studentMajor, Wrappers.<UserMajor>lambdaUpdate()
                .set(UserMajor::getUserPicture,studentMajor.getUserPicture())
                .set(UserMajor::getUserNumber,studentMajor.getUserNumber())
                .set(UserMajor::getThesisFile,studentMajor.getThesisFile())
                .set(UserMajor::getThesisName,studentMajor.getThesisName())
                .set(UserMajor::getThesisScore,studentMajor.getThesisScore())
                .eq(UserMajor::getId,studentMajor.getId()));

        if(!byId.getMajorId().equals(studentMajor.getMajorId()) || !byId.getUserId().equals(studentMajor.getUserId())){

            //删掉用户专业课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getUserId,byId.getUserId()).eq(UserCourse::getMajorId,byId.getMajorId()));

            //新增用户专业课程
            List<UserMajor> userMajors = new ArrayList<>();
            userMajors.add(studentMajor);
            this.addUserCourse(userMajors);

            if(byId.getUserId().equals(studentMajor.getUserId())){
                //同步相同课程进度
                this.addUserCourseProgress(byId.getMajorId(),studentMajor.getMajorId(),studentMajor.getUserId());
            }
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getUserId, byId.getUserId()).eq(UserLearning::getMajorId, byId.getMajorId()));
        }
        return true;
    }

    private void addUserCourseProgress(Integer oldMajor, Integer newMajorId, Integer userId){
        List<UserLearning> list = userLearningService.list(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getUserId, userId).eq(UserLearning::getMajorId, oldMajor));
        if(!CollectionUtils.isEmpty(list)){
            Set<Integer> courseIds = new HashSet<>();
            Map<Integer,List<UserLearning>> courseIdMap = new HashMap<>();
            for(UserLearning userLearning:list){
                courseIds.add(userLearning.getCourseId());
                List<UserLearning> userLearnings = courseIdMap.get(userLearning.getCourseId());
                if(!CollectionUtils.isEmpty(userLearnings)){
                    userLearnings.add(userLearning);
                }else{
                    userLearnings = new ArrayList<>();
                    userLearnings.add(userLearning);
                    courseIdMap.put(userLearning.getCourseId(),userLearnings);
                }
            }

            List<UserCourse> list1 = userCourseService.list(new LambdaQueryWrapper<UserCourse>()
                    .eq(UserCourse::getUserId, userId)
                    .eq(UserCourse::getMajorId, newMajorId).in(UserCourse::getCourseId, courseIds));
            if(!CollectionUtils.isEmpty(list1)){
                List<UserLearning> addList = new ArrayList<>();
                for(UserCourse userCourse:list1){
                    List<UserLearning> userLearnings = courseIdMap.get(userCourse.getCourseId());
                    if(!CollectionUtils.isEmpty(userLearnings)){
                        for(UserLearning userLearning:userLearnings){
                            userLearning.setId(null);
                            userLearning.setMajorId(newMajorId);
                            userLearning.setCreateTime(null);
                            userLearning.setUpdateTime(null);
                            addList.add(userLearning);
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(addList)){
                    userLearningService.saveBatch(addList,5000);
                }
            }
        }
    }

    @Override
    public UserMajor getByIdRel(Integer id) {
        UserMajorParam param = new UserMajorParam();
        param.setId(id);
        return param.getOne(baseMapper.selectListRel(param));
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

            List<String> userList = new ArrayList<>();
            Set<String> orgList = new HashSet<>();

            List<String> userNumberList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    userList.add(item[0]);
                }
                if(StringUtils.hasText(item[1])){
                    userNumberList.add(item[1]);
                }
                if(StringUtils.hasText(item[2])){
                    orgList.add(item[2]);
                }
                if(StringUtils.hasText(item[3])){
                    String[] s = item[3].split("_");
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
            }

            //学号
            Map<String,UserMajor> userNumberMap =new HashMap<>();
            Map<String,UserMajor> excelUserNumberMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(userNumberList)){
                List<UserMajor> userMajors = this.baseMapper.selectList(Wrappers.<UserMajor>lambdaQuery().in(UserMajor::getUserNumber, userNumberList));
                if(!CollectionUtils.isEmpty(userMajors)){
                    for (UserMajor userMajor:userMajors){
                        userNumberMap.put(userMajor.getUserNumber(),userMajor);
                    }
                }
            }

            // KEY_专业
            Map<String, Major> majorMap = new HashMap<>();
            List<Integer> majorIds = new ArrayList<>();
            if(!CollectionUtils.isEmpty(majorYearList)&& !CollectionUtils.isEmpty(majorCodeList) && !CollectionUtils.isEmpty(majorNameList)&& !CollectionUtils.isEmpty(majorTypeList)
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
                        majorIds.add(_major.getMajorId());
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

            //站点
            Map<String,Org> orgMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(orgList)){
                List<Org> orgExists = orgService.list(new LambdaQueryWrapper<Org>().in(Org::getOrgFullName,orgList));
                if(!CollectionUtils.isEmpty(orgExists)){
                    for(Org org:orgExists){
                        orgMap.put(org.getOrgFullName(),org);
                    }
                }
            }

            //用户专业
            Map<String,UserMajor> hasUserMajorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(userIds) && !CollectionUtils.isEmpty(majorIds)){
                List<UserMajor> userMajors = this.baseMapper.selectList(Wrappers.<UserMajor>lambdaQuery()
                        .in(UserMajor::getMajorId, majorIds)
                        .in(UserMajor::getUserId, userIds));
                if(!CollectionUtils.isEmpty(userMajors)){
                    for (UserMajor userMajor:userMajors){
                        hasUserMajorMap.put(userMajor.getUserId()+"_"+userMajor.getMajorId(),userMajor);
                    }
                }
            }

            int lastIndex = list.get(2).length;
            Map<String,UserMajor> userMajorMap = new HashMap<>();
            List<UserMajor> rowList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                UserMajor userMajor = new UserMajor();
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
                            userMajor.setUserId(_user.getUserId());
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

                if(StringUtils.hasText(arr[1])){
                    UserMajor userMajor1 = userNumberMap.get(arr[1]);
                    if(userMajor1 != null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "学号唯一重复", style);
                        continue;
                    }else{
                        UserMajor userMajor2 = excelUserNumberMap.get(arr[1]);
                        if(userMajor2 != null){
                            excellReader.setCellValue(sheetIndex, i, lastIndex, "学号唯一重复导入", style);
                            continue;
                        }else{
                            excelUserNumberMap.put(arr[1],userMajor);
                        }
                    }
                    userMajor.setUserNumber(arr[1]);
                }

                if(!StringUtils.hasText(arr[2])){
                    //判断当前Excel列表是否重复
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "站点不能为空", style);
                    continue;
                }else{
                    Org org = orgMap.get(arr[2]);
                    if(org == null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "站点未找到", style);
                        continue;
                    }
                    userMajor.setOrgId(org.getOrgId());
                }

                if(!StringUtils.hasText(arr[3])){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "专业不能为空", style);
                    continue;
                }else{
                    Major major = majorMap.get(arr[3]);
                    if(major == null){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "专业未找到", style);
                        continue;
                    }
                    userMajor.setMajorId(major.getMajorId());
                }
                String key = userMajor.getUserId() + "_" + userMajor.getMajorId();

                UserMajor userMajor1 = userMajorMap.get(key);
                if(userMajor1 != null){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-专业导入重复", style);
                    continue;
                }else{
                    userMajorMap.put(key,userMajor);
                }

                UserMajor userMajor2 = hasUserMajorMap.get(key);
                if(userMajor2 != null){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-专业数据已经存在", style);
                    continue;
                }

                excellReader.removeRow(0,i);
                rowList.add(userMajor);
            }

            if (!rowList.isEmpty()) {
                this.saveBatch(rowList,5000);
                this.addUserCourse(rowList);
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("用户专业导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
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
    public void templateExport(HttpServletResponse res) {
        try {
            ExcelOperation entity = new ExcelOperation("/user_major_template.xlsx");

            // order by major_year desc
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
                entity.generateRangeList(3, majorArr,3,null,true,0);
            }

            List<Org> orgList = orgService.list();
            if(!CollectionUtils.isEmpty(orgList)){
                String[] orgArr = new String[orgList.size()];
                int index = 0;
                for (Org org : orgList) {
                    orgArr[index] = org.getOrgFullName();
                    index++;
                }
                entity.generateRangeList(2, orgArr,3,null,true,0);
            }

            entity.print(res, "用户专业导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    @Override
    public ApiResult<?> thesisImportBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res) {
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

            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    userList.add(item[0]);
                }
                if(StringUtils.hasText(item[1])){
                    majorYearList.add(item[1]);
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

            //用户专业
            Map<String,List<UserMajor>> hasUserMajorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(userIds) && !CollectionUtils.isEmpty(majorYearList)){
                UserMajorParam param = new UserMajorParam();
                param.setUserIds(userIds);
                param.setMajorYears(new ArrayList<>(majorYearList));
                List<UserMajor> userMajors = this.baseMapper.selectListRel(param);
                if(!CollectionUtils.isEmpty(userMajors)){
                    for (UserMajor userMajor:userMajors){
                        String key = userMajor.getUserId()+"_"+userMajor.getMajorYear();
                        List<UserMajor> userMajors1 = hasUserMajorMap.get(key);
                        if(CollectionUtils.isEmpty(userMajors1)){
                            userMajors1 = new ArrayList<>();
                            userMajors1.add(userMajor);
                            hasUserMajorMap.put(key,userMajors1);
                        }else{
                            userMajors1.add(userMajor);
                        }
                    }
                }
            }

            int lastIndex = list.get(2).length;
            Map<String,UserMajor> userMajorMap = new HashMap<>();
            List<UserMajor> rowList = new ArrayList<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                Integer userId =null;
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

                if(!StringUtils.hasText(arr[3])){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "论文成绩不能为空", style);
                    continue;
                }

                String key = userId + "_" + arr[1];
                List<UserMajor> userMajorLists = hasUserMajorMap.get(key);
                if(CollectionUtils.isEmpty(userMajorLists)){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-招生年份不存在专业", style);
                    continue;
                }

                if(userMajorLists.size()>1){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-招生年份下找到二个专业", style);
                    continue;
                }

                UserMajor _userMajor = userMajorLists.get(0);

                UserMajor userMajor = new UserMajor();
                userMajor.setId(_userMajor.getId());
                userMajor.setThesisScore(arr[3]);
                if(StringUtils.hasText(arr[2])){
                    userMajor.setThesisName(arr[2]);
                }

                UserMajor userMajor1 = userMajorMap.get(key);
                if(userMajor1 != null){
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "用户-论文成绩导入重复", style);
                    continue;
                }else{
                    userMajorMap.put(key,userMajor);
                }

                excellReader.removeRow(0,i);
                rowList.add(userMajor);
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
    public void thesisTemplateExport(HttpServletResponse response) {
        try {
            ExcelOperation entity = new ExcelOperation("/user_major_thesis_template.xlsx");

            entity.print(response, "用户论文成绩导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    private void addUserCourse(List<UserMajor> userMajors){
        if(!CollectionUtils.isEmpty(userMajors)){
            Set<Integer> majorIds = new HashSet<>();
            for(UserMajor userMajor:userMajors){
                majorIds.add(userMajor.getMajorId());
            }
            List<MajorCourse> list = majorCourseService.list(new LambdaQueryWrapper<MajorCourse>().in(MajorCourse::getMajorId, majorIds));
            if(!CollectionUtils.isEmpty(list)){
                Map<Integer,List<MajorCourse>> majorCourseMap =new HashMap<>();
                for (MajorCourse majorCourse:list){
                    List<MajorCourse> majorCourses1 = majorCourseMap.get(majorCourse.getMajorId());
                    if(majorCourses1 == null){
                        majorCourses1 = new ArrayList<>();
                        majorCourses1.add(majorCourse);
                        majorCourseMap.put(majorCourse.getMajorId(),majorCourses1);
                    }else{
                        majorCourses1.add(majorCourse);
                    }
                }
                List<UserCourse> _userCourseList = new ArrayList<>();
                for(UserMajor userMajor:userMajors){
                    List<MajorCourse> majorCourses1 = majorCourseMap.get(userMajor.getMajorId());
                    if(!CollectionUtils.isEmpty(majorCourses1)){
                        for(MajorCourse majorCourse:majorCourses1){
                            UserCourse userCourse = new UserCourse();
                            userCourse.setCourseId(majorCourse.getCourseId());
                            userCourse.setUserId(userMajor.getUserId());
                            userCourse.setMajorId(userMajor.getMajorId());
                            _userCourseList.add(userCourse);
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(_userCourseList)){
                    userCourseService.saveBatch(_userCourseList,5000);
                }
            }
        }
    }


    private void initOther(List<UserMajor> studentMajors){
        if(!CollectionUtils.isEmpty(studentMajors)){
            Set<Integer> orgIds = new HashSet<>();
            Set<Integer> userIds = new HashSet<>();
            Set<Integer> majorIds = new HashSet<>();
            for (UserMajor studentMajor : studentMajors) {
                if(studentMajor.getOrgId() !=null){
                    orgIds.add(studentMajor.getOrgId());
                }
                userIds.add(studentMajor.getUserId());
                majorIds.add(studentMajor.getMajorId());
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

            Map<Integer, Org> orgMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(orgIds)){
                List<Org> orgs = orgService.listByIds(orgIds);
                if (!CollectionUtils.isEmpty(orgs)) {
                    orgMap = orgs.stream().collect(Collectors.toMap(Org::getOrgId, Org -> Org));
                }
            }

            for (UserMajor studentMajor : studentMajors) {
                studentMajor.setMajor(majorMap.get(studentMajor.getMajorId()));
                User user = userMap.get(studentMajor.getUserId());
                if(user!=null){
                    user.setPassword(null);
                    studentMajor.setUser(user);
                }
                if(studentMajor.getOrgId() !=null){
                    studentMajor.setOrg(orgMap.get(studentMajor.getOrgId()));
                }
            }
        }
    }

    /**
     * 生成登记表
     * @param majorId
     * @param loginUser
     * @return
     * @throws IOException
     */
    public WordOperation generateSingleRegistrationFile(Integer  majorId, User loginUser) throws IOException {

        //用户的专业
        long count = this.count(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getUserId, loginUser.getUserId())
                .eq(UserMajor::getMajorId, majorId));
        if(count == 0){
            throw new BusinessException("用户不存在当前专业");
        }

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("xm", loginUser.getRealname());
        testMap.put("xb", loginUser.getSex());
        testMap.put("mz", loginUser.getNation());
        testMap.put("zzmm", loginUser.getPolitics());
        testMap.put("jtzz", loginUser.getAddress());
        testMap.put("gzdw", loginUser.getOrganization());
        if(loginUser.getBirthday() != null){
            testMap.put("csrq", CommonUtil.dateToString(loginUser.getBirthday(),CommonUtil.ymd));
        }
        if(StringUtils.hasText(loginUser.getIdPhoto())){
            testMap.put("tx", new ImgObject("证件照",loginUser.getIdPhoto(),100,120));
        }
        if(StringUtils.hasText(loginUser.getIdCard1())){
            testMap.put("sfzzm", new ImgObject("身份证正面",loginUser.getIdCard1(),160,120));
        }
        if(StringUtils.hasText(loginUser.getIdCard2())){
            testMap.put("sfzfm", new ImgObject("身份证反面",loginUser.getIdCard2(),160,120));
        }

        Major byId = majorService.getById(majorId);
        String rxyf = "0115";
        if(byId != null){
            testMap.put("nf", byId.getMajorYear());
            testMap.put("zy", byId.getMajorName());
            testMap.put("cc", byId.getMajorGradation());
            testMap.put("xz", byId.getMajorLength());
            testMap.put("xs", byId.getMajorForms());
            testMap.put("rxsj", byId.getMajorYear()+rxyf);

            // 毕业时间、毕业年份计算
            Double majorLength = Double.valueOf(byId.getMajorLength());
            int bynf = Integer.valueOf(byId.getMajorYear())+(int)Math.floor(majorLength);
            Date byDate = CommonUtil.StringTodate(bynf + rxyf, "yyyyMMdd");
            if(majorLength>Math.floor(majorLength)){
                Calendar calendar = Calendar.getInstance();
                // 设置calendar的日期为当前日期
                calendar.setTime(byDate); // 或者使用特定的日期
                // 添加6个月
                calendar.add(Calendar.MONTH, 6);
                byDate = calendar.getTime();
            }
            testMap.put("bysj", CommonUtil.dateToString(byDate,"yyyyMMdd"));
            testMap.put("bynf", CommonUtil.dateToString(byDate,"yyyy年MM月dd日"));
        }

        Tenant tenant = tenantService.getById(ThreadLocalContextHolder.getTenant());
        if(tenant != null){
            testMap.put("xxmc", tenant.getTenantName());
        }

        WordOperation wordOperation = new WordOperation("/register.doc");
        wordOperation.changWord(testMap,new ArrayList<>());
        return wordOperation;
    }

    /**
     * 生成学籍表
     * @param majorId
     * @param loginUser
     * @return
     * @throws IOException
     */
    public WordOperation generateSingleEnrollmentFile(Integer  majorId, User loginUser) throws IOException {

        //用户的专业
        long count = this.count(new LambdaQueryWrapper<UserMajor>()
                .eq(UserMajor::getUserId, loginUser.getUserId())
                .eq(UserMajor::getMajorId, majorId));
        if(count == 0){
            throw new BusinessException("用户不存在当前专业");
        }

        Map<String, Object> testMap = new HashMap<>();

        testMap.put("xm", loginUser.getRealname());
        testMap.put("xb", loginUser.getSex());
        testMap.put("mz", loginUser.getNation());
        testMap.put("zzmm", loginUser.getPolitics());
        testMap.put("jtzz", loginUser.getAddress());
        testMap.put("gzdw", loginUser.getOrganization());
        if(loginUser.getBirthday() != null){
            testMap.put("csrq", CommonUtil.dateToString(loginUser.getBirthday(),CommonUtil.ymd));
        }
        if(StringUtils.hasText(loginUser.getIdPhoto())){
            testMap.put("tx", new ImgObject("证件照",loginUser.getIdPhoto(),100,120));
        }

        Major byId = majorService.getById(majorId);
        String rxyf = "0115";
        if(byId != null){
            testMap.put("nf", byId.getMajorYear());
            testMap.put("zy", byId.getMajorName());
            testMap.put("cc", byId.getMajorGradation());
            testMap.put("xz", byId.getMajorLength());
            testMap.put("xs", byId.getMajorForms());
            testMap.put("rxsj", byId.getMajorYear()+rxyf);

            // 毕业时间、毕业年份计算
            Double majorLength = Double.valueOf(byId.getMajorLength());
            int bynf = Integer.valueOf(byId.getMajorYear())+(int)Math.floor(majorLength);
            Date byDate = CommonUtil.StringTodate(bynf + rxyf, "yyyyMMdd");
            if(majorLength>Math.floor(majorLength)){
                Calendar calendar = Calendar.getInstance();
                // 设置calendar的日期为当前日期
                calendar.setTime(byDate); // 或者使用特定的日期
                // 添加6个月
                calendar.add(Calendar.MONTH, 6);
                byDate = calendar.getTime();
            }
            testMap.put("bysj", CommonUtil.dateToString(byDate,"yyyyMMdd"));
            testMap.put("bynf", CommonUtil.dateToString(byDate,"yyyy年MM月dd日"));
        }

        Tenant tenant = tenantService.getById(ThreadLocalContextHolder.getTenant());
        if(tenant != null){
            testMap.put("xxmc", tenant.getTenantName());
        }

        WordOperation wordOperation = new WordOperation("/enrollment.doc");
        wordOperation.changWord(testMap,new ArrayList<>());
        return wordOperation;
    }

}

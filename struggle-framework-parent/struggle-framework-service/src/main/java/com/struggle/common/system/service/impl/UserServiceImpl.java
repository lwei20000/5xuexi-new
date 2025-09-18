package com.struggle.common.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.excel.ExcelConstant;
import com.struggle.common.core.excel.ExcelOperation;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.FileDfsUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.mapper.UserMapper;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.param.UserParam;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private UserOrgService userOrgService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private RoleOrgService roleOrgService;
    @Resource
    private OrgLeaderService orgLeaderService;
    @Resource
    private RoleService roleService;
    @Resource
    private OrgService orgService;
    @Resource
    private GroupService groupService;
    @Resource
    private DictionaryDataService dictionaryDataService;
    @Resource
    private FileDfsUtil fileDfsUtil;

    @Override
    public PageResult<User> pageRel(UserParam param,boolean detail) {
        PageParam<User, UserParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        List<User> list = baseMapper.selectPageRel(page, param,null,null);
        if(detail){
            // 查询用户的机构、角色
            this.selectUserOthers(list);
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<User> listRel(UserParam param,boolean detail) {
        List<User> list = baseMapper.selectListRel(param,null,null);
        if(detail){
            // 查询用户的机构、角色、分组
            this.selectUserOthers(list);
        }
        // 排序
        PageParam<User, UserParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    /**
     * 登录用
     * @return
     */
    @Override
    public User getByIdRel(Integer userId) {
        User user = baseMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            user.setUpdateTime(null);
            //当前租户与用户的关系
            UserTenant userTenant = userTenantService.getOne(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getCreateTime).eq(UserTenant::getUserId, userId));
            if(userTenant != null){
                user.setUserTenantCreateTime(userTenant.getCreateTime());
            }
            //所在机构
            List<Org> _orgs = userOrgService.listByUserId(user.getUserId());
            user.setOrgs(_orgs);

            //所在分组
            List<Group> _groups = userGroupService.listByUserId(user.getUserId());
            user.setGroups(_groups);

            //角色
            List<Role> roles = userRoleService.listByUserId(user.getUserId());
            if(!CollectionUtils.isEmpty(roles)){
                for(Role role:roles){
                    //查询角色 权限的机构
                    Set<Org> orgs = new HashSet<>();
                    if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_1.getCode())){
                        //不用处理
                    }else if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_2.getCode())){
                        //不用处理
                    }else if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_7.getCode()) || role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_8.getCode())){
                        orgs.addAll(roleOrgService.listByRoleId(role.getRoleId()));
                    }else  if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_5.getCode()) || role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_5.getCode())){
                        orgs.addAll(orgLeaderService.listByUserId(user.getUserId()));
                    }else  if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_3.getCode()) || role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_4.getCode())){
                        orgs.addAll(_orgs);
                    }
                    role.setOrgs(new ArrayList<>(orgs));
                }
                user.setRoles(roles);
            }
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return  baseMapper.selectByUsername(username);
    }


    @Transactional
    @Override
    public boolean saveUser(User user) {
        if(StrUtil.isEmpty(user.getUsername())){
            throw new BusinessException("账号不能为空");
        }
        User _user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if(_user != null){
            long one = userTenantService.count(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getUserId, _user.getUserId()));
            if(one > 0){
                throw new BusinessException("账号已存在");
            }
            user.setUserId(_user.getUserId());
        }else{
            if (StrUtil.isNotEmpty(user.getUsername()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, user.getUsername())) > 0) {
                throw new BusinessException("账号已存在");
            }
            if (StrUtil.isNotEmpty(user.getPhone()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, user.getPhone())) > 0) {
                throw new BusinessException("手机号已存在");
            }
            if (StrUtil.isNotEmpty(user.getIdCard()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getIdCard, user.getIdCard())) > 0) {
                throw new BusinessException("身份证件号已存在");
            }
            if (StrUtil.isNotEmpty(user.getEmail()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, user.getEmail())) > 0) {
                throw new BusinessException("邮箱已存在");
            }
            baseMapper.insert(user);
        }

        //新增与租户的关系
        UserTenant u = new UserTenant();
        u.setUserId(user.getUserId());
        u.setUserType(user.getUserType());
        userTenantService.save(u);

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            List<Integer> roleIds = user.getRoles().stream().map(Role::getRoleId).collect(Collectors.toList());
            if (userRoleService.saveBatch(user.getUserId(), roleIds) < roleIds.size()) {
                throw new BusinessException("用户角色添加失败");
            }
        }
        if (!CollectionUtils.isEmpty(user.getOrgs())) {
            List<Integer> orgIds = user.getOrgs().stream().map(Org::getOrgId).collect(Collectors.toList());
            if (userOrgService.saveBatch(user.getUserId(), orgIds) < orgIds.size()) {
                throw new BusinessException("用户机构添加失败");
            }
        }
        if (!CollectionUtils.isEmpty(user.getGroups())) {
            List<Integer> groupIds = user.getGroups().stream().map(Group::getGroupId).collect(Collectors.toList());
            if (userGroupService.saveBatch(user.getUserId(), groupIds) < groupIds.size()) {
                throw new BusinessException("用户分组添加失败");
            }
        }
        return true;
    }

    @Transactional
    @Override
    public boolean updateUser(User user) {
        if (StrUtil.isNotEmpty(user.getUsername()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername())
                .ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("账号已存在");
        }
        if (StrUtil.isNotEmpty(user.getIdCard()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getIdCard, user.getIdCard())
                .ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("身份证号已存在");
        }
        if (StrUtil.isNotEmpty(user.getPhone()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone())
                .ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("手机号已存在");
        }
        if (StrUtil.isNotEmpty(user.getEmail()) && baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, user.getEmail())
                .ne(User::getUserId, user.getUserId())) > 0) {
            throw new BusinessException("邮箱已存在");
        }
        long one = userTenantService.count(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getUserId, user.getUserId()));
        if(one == 0){
            throw new BusinessException("没有修改权限");
        }

        userTenantService.update(null,Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getUserType,user.getUserType())
                .eq(UserTenant::getUserId,user.getUserId()));

        user.setUpdateTime(null);
        boolean result = baseMapper.update(user, Wrappers.<User>lambdaUpdate()
                .set(User::getRealname,user.getRealname())
                .set(User::getEmail,user.getEmail())
                .set(User::getIdCard,user.getIdCard())
                .set(User::getPhone,user.getPhone())
                .set(User::getSex,user.getSex())
                .set(User::getBirthday,user.getBirthday())
                .set(User::getOrganization,user.getOrganization())
                .set(User::getAddress,user.getAddress())
                .set(User::getNation,user.getNation())
                .set(User::getPolitics,user.getPolitics())
                .set(User::getIdPhoto,user.getIdPhoto())
                .set(User::getIdCard1,user.getIdCard1())
                .set(User::getIdCard2,user.getIdCard2())
                .set(User::getIntroduction,user.getIntroduction())
                .eq(User::getUserId,user.getUserId())) > 0;

        if(result){
            userRoleService.remove(new LambdaUpdateWrapper<UserRole>().eq(UserRole::getUserId, user.getUserId()));
            if (user.getRoles() != null && user.getRoles().size() > 0) {
                List<Integer> roleIds = user.getRoles().stream().map(Role::getRoleId).collect(Collectors.toList());
                if (userRoleService.saveBatch(user.getUserId(), roleIds) < roleIds.size()) {
                    throw new BusinessException("用户角色添加失败");
                }
            }
            userOrgService.remove(new LambdaUpdateWrapper<UserOrg>().eq(UserOrg::getUserId, user.getUserId()));
            if (user.getOrgs() != null && user.getOrgs().size() > 0) {
                List<Integer> orgIds = user.getOrgs().stream().map(Org::getOrgId).collect(Collectors.toList());
                if (userOrgService.saveBatch(user.getUserId(), orgIds) < orgIds.size()) {
                    throw new BusinessException("用户机构添加失败");
                }
            }
            userGroupService.remove(new LambdaUpdateWrapper<UserGroup>().eq(UserGroup::getUserId, user.getUserId()));
            if (user.getGroups() != null && user.getGroups().size() > 0) {
                List<Integer> groupIds = user.getGroups().stream().map(Group::getGroupId).collect(Collectors.toList());
                if (userGroupService.saveBatch(user.getUserId(), groupIds) < groupIds.size()) {
                    throw new BusinessException("用户分组添加失败");
                }
            }
        }

        return result;
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

            List<String> usernameList = new ArrayList<>();
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();
            List<String> idCardList = new ArrayList<>();
            Set<String> roleList = new HashSet<>();
            Set<String> orgList = new HashSet<>();
            Set<String> groupList = new HashSet<>();
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] item = list.get(i);
                if(StringUtils.hasText(item[0])){
                    usernameList.add(item[0]);
                }
                if(StringUtils.hasText(item[6])){
                    idCardList.add(item[6]);
                }
                if(StringUtils.hasText(item[7])){
                    phoneList.add(item[7]);
                }
                if(StringUtils.hasText(item[8])){
                    emailList.add(item[8]);
                }
                if(StringUtils.hasText(item[13])){
                    String[] split = item[13].split(",");
                    roleList.addAll(Arrays.asList(split));
                }
                if(StringUtils.hasText(item[14])){
                    String[] split = item[14].split(",");
                    orgList.addAll(Arrays.asList(split));
                }
                if(StringUtils.hasText(item[15])){
                    String[] split = item[15].split(",");
                    groupList.addAll(Arrays.asList(split));
                }
            }
            // 用户名_用户
            Map<String,User> usernameMap = new HashMap<>(usernameList.size());
            Map<String,User> excelUsernameMap = new HashMap<>();
            Map<Integer,UserTenant> userTenantMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(usernameList)){
                List<User> usernameExists = baseMapper.selectList(new LambdaQueryWrapper<User>()
                        .select(User::getUserId,User::getUsername)
                        .in(User::getUsername,usernameList));
                if(!CollectionUtils.isEmpty(usernameExists)){
                    List<Integer> userIds = new ArrayList<>(usernameExists.size());
                    for(User user:usernameExists){
                        usernameMap.put(user.getUsername(),user);
                        userIds.add(user.getUserId());
                    }
                    List<UserTenant> userTenants = userTenantService.list(Wrappers.<UserTenant>lambdaQuery()
                            .select(UserTenant::getUserId)
                            .in(UserTenant::getUserId, userIds));
                    if(!CollectionUtils.isEmpty(userTenants)){
                        for (UserTenant userTenant:userTenants){
                            userTenantMap.put(userTenant.getUserId(),userTenant);
                        }
                    }
                }
            }
            // 手机号_用户
            Map<String,User> phoneMap = new HashMap<>(phoneList.size());
            Map<String,User> excelPhoneMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(phoneList)){
                List<User> usernameExists = baseMapper.selectList(new LambdaQueryWrapper<User>()
                        .select(User::getUserId,User::getPhone)
                        .in(User::getPhone,phoneList));
                if(!CollectionUtils.isEmpty(usernameExists)){
                    for(User user:usernameExists){
                        phoneMap.put(user.getPhone(),user);
                    }
                }
            }
            // 邮箱_用户
            Map<String,User> emailMap = new HashMap<>(emailList.size());
            Map<String,User> excelEmailMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(emailList)){
                List<User> usernameExists = baseMapper.selectList(new LambdaQueryWrapper<User>()
                        .select(User::getUserId,User::getEmail)
                        .in(User::getEmail,emailList));
                if(!CollectionUtils.isEmpty(usernameExists)){
                    for(User user:usernameExists){
                        emailMap.put(user.getEmail(),user);
                    }
                }
            }
            // 身份证_用户
            Map<String,User> idCardMap = new HashMap<>(idCardList.size());
            Map<String,User> excelIdCardMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(idCardList)){
                List<User> usernameExists = baseMapper.selectList(new LambdaQueryWrapper<User>()
                                .select(User::getUserId,User::getIdCard)
                        .in(User::getIdCard,idCardList));
                if(!CollectionUtils.isEmpty(usernameExists)){
                    for(User user:usernameExists){
                        idCardMap.put(user.getIdCard(),user);
                    }
                }
            }
            //角色
            Map<String,Role> roleMap = new HashMap<>(roleList.size());
            if(!CollectionUtils.isEmpty(roleList)){
                List<Role> roleExists = roleService.list(new LambdaQueryWrapper<Role>()
                        .select(Role::getRoleId,Role::getRoleName)
                        .in(Role::getRoleName,roleList));
                if(!CollectionUtils.isEmpty(roleExists)){
                    for(Role role:roleExists){
                        roleMap.put(role.getRoleName(),role);
                    }
                }
            }
            //机构
            Map<String,Org> orgMap = new HashMap<>(orgList.size());
            if(!CollectionUtils.isEmpty(orgList)){
                List<Org> orgExists = orgService.list(new LambdaQueryWrapper<Org>()
                        .select(Org::getOrgFullName,Org::getOrgId)
                        .in(Org::getOrgFullName,orgList));
                if(!CollectionUtils.isEmpty(orgExists)){
                    for(Org org:orgExists){
                        orgMap.put(org.getOrgFullName(),org);
                    }
                }
            }
            //分组
            Map<String,Group> groupMap = new HashMap<>(groupList.size());
            if(!CollectionUtils.isEmpty(groupList)){
                List<Group> groupExists = groupService.list(new LambdaQueryWrapper<Group>()
                        .select(Group::getGroupName,Group::getGroupId)
                        .in(Group::getGroupName,groupList));
                if(!CollectionUtils.isEmpty(groupExists)){
                    for(Group group:groupExists){
                        groupMap.put(group.getGroupName(),group);
                    }
                }
            }

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("user_type");
            List<DictionaryData> dictionaryDatas = dictionaryDataService.listRel(param);
            Map<String,String> dictionaryDataMap = new HashMap<>();
            for (DictionaryData dictionaryData:dictionaryDatas){
                dictionaryDataMap.put(dictionaryData.getDictDataName(),dictionaryData.getDictDataCode());
            }

            int lastIndex = list.get(2).length;
            List<User> rowList = new ArrayList<>(list.size());
            for (int i = list.size() - 1; i >= 3; i--) {
                String[] arr = list.get(i);
                User user = new User();
                user.setUsername(arr[0]);
                user.setPassword(arr[1]);
                user.setRealname(arr[2]);
                user.setSex(arr[4]);
                //user.setBirthday(arr[5]);
                user.setIdCard(arr[6]);
                user.setPhone(arr[7]);
                user.setEmail(arr[8]);
                user.setNation(arr[9]);
                user.setPolitics(arr[10]);
                user.setOrganization(arr[11]);
                user.setAddress(arr[12]);

                if(!StringUtils.hasText(user.getUsername())){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "登录账号不能为空", style);
                    continue;
                }else{
                    User _user = usernameMap.get(user.getUsername());
                    if(_user != null){
                        UserTenant userTenant = userTenantMap.get(_user.getUserId());
                        if(userTenant != null){
                            //判断是否找到对应代码
                            excellReader.setCellValue(sheetIndex, i, lastIndex, "登录账号唯一重复", style);
                            continue;
                        }else{
                            //当前租户没有
                            user.setUserId(_user.getUserId());
                        }
                    }
                }
                if(!StringUtils.hasText(user.getPassword())){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "登录密码不能为空", style);
                    continue;
                }else{
                    user.setPassword(CommonUtil.encodePassword(user.getPassword()));
                }

                if(!StringUtils.hasText(user.getRealname())){
                    //判断是否找到对应代码
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "姓名不能为空", style);
                    continue;
                }

                if(StringUtils.hasText(user.getSex())){
                    if(!"男".equals(user.getSex()) && !"女".equals(user.getSex())) {
                        //判断是否找到对应代码
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "性别错误，只能是：男、女", style);
                        continue;
                    }
                }

                if(StringUtils.hasText(arr[3])){
                    String[] split = arr[3].split(",");
                    Set<String> userTypes = new HashSet<>();
                    String msg = "";
                    for(String type : split){
                        String s = dictionaryDataMap.get(type);
                        if(!StringUtils.hasText(s)){
                            msg+="用户类型["+type+"]系统中未找到;";
                        }
                        userTypes.add(s);
                    }
                    if(StringUtils.hasText(msg)){
                        //判断是否找到对应代码
                        excellReader.setCellValue(sheetIndex, i, lastIndex, msg, style);
                        continue;
                    }
                    user.setUserType(JSONUtil.toJSONString(userTypes.toArray(new String[userTypes.size()])));
                }
                if(StringUtils.hasText(arr[5])){
                    Date date = CommonUtil.StringTodate(arr[5], CommonUtil.ymd);
                    if(date == null){
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "出生日期错误 格式：2010-09-01", style);
                        continue;
                    }else{
                        user.setBirthday(date);
                    }
                }

                //用户名重复导入校验
                User obj = excelUsernameMap.get(user.getUsername());
                if (obj != null) {
                    //判断当前Excel列表是否重复
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "登录账号唯一重复导入", style);
                    continue;
                } else {
                    excelUsernameMap.put(user.getUsername(), user);
                }

                //手机号重复导入校验
                if(StringUtils.hasText(user.getPhone())){
                    User _obj = excelPhoneMap.get(user.getPhone());
                    if (_obj != null) {
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "手机号唯一重复导入", style);
                        continue;
                    } else {
                        excelPhoneMap.put(user.getPhone(), user);
                    }
                    User _user = phoneMap.get(user.getPhone());
                    if(_user != null && !_user.getUserId().equals(user.getUserId())){
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "手机号唯一重复", style);
                        continue;
                    }
                }

                //邮箱重复导入校验
                if(StringUtils.hasText(user.getEmail())){
                    User e_obj = excelEmailMap.get(user.getEmail());
                    if (e_obj != null) {
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "邮箱唯一重复导入", style);
                        continue;
                    } else {
                        excelEmailMap.put(user.getEmail(), user);
                    }
                    User _user = emailMap.get(user.getEmail());
                    if(_user != null && !_user.getUserId().equals(user.getUserId())){
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "邮箱唯一重复", style);
                        continue;
                    }
                }
                //身份证重复导入校验
                if(StringUtils.hasText(user.getIdCard())){
                    User e_obj = excelIdCardMap.get(user.getIdCard());
                    if (e_obj != null) {
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "身份证唯一重复导入", style);
                        continue;
                    } else {
                        excelIdCardMap.put(user.getIdCard(), user);
                    }
                    User _user = idCardMap.get(user.getIdCard());
                    if(_user != null && !_user.getUserId().equals(user.getUserId())){
                        //判断当前Excel列表是否重复
                        excellReader.setCellValue(sheetIndex, i, lastIndex, "身份证唯一重复", style);
                        continue;
                    }
                }

                if(!StringUtils.hasText(arr[13])){
                    //判断当前Excel列表是否重复
                    excellReader.setCellValue(sheetIndex, i, lastIndex, "角色不能为空", style);
                    continue;
                }else{
                    String[] split = arr[13].split(",");
                    String msg = "";
                    Set<Role> roles = new HashSet<>();
                    for(String name : split){
                        Role role = roleMap.get(name);
                        if(role == null){
                            msg+="角色["+name+"]系统中未找到;";
                        }
                        roles.add(role);
                    }
                    if(StringUtils.hasText(msg)){
                        //判断是否找到对应代码
                        excellReader.setCellValue(sheetIndex, i, lastIndex, msg, style);
                        continue;
                    }
                    user.setRoles(new ArrayList<>(roles));
                }
                if(StringUtils.hasText(arr[14])){
                    String[] split = arr[14].split(",");
                    String msg = "";
                    Set<Org> orgs = new HashSet<>();
                    for(String name : split){
                        Org org = orgMap.get(name);
                        if(org == null){
                            msg+="机构["+name+"]系统中未找到;";
                        }
                        orgs.add(org);
                    }
                    if(StringUtils.hasText(msg)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, msg, style);
                        continue;
                    }
                    user.setOrgs(new ArrayList<>(orgs));
                }

                if(StringUtils.hasText(arr[15])){
                    String[] split = arr[15].split(",");
                    String msg = "";
                    Set<Group> groups = new HashSet<>();
                    for(String name : split){
                        Group group = groupMap.get(name);
                        if(group == null){
                            msg+="分组["+name+"]系统中未找到;";
                        }
                        groups.add(group);
                    }
                    if(StringUtils.hasText(msg)){
                        excellReader.setCellValue(sheetIndex, i, lastIndex, msg, style);
                        continue;
                    }
                    user.setGroups(new ArrayList<>(groups));
                }

                excellReader.removeRow(0,i);
                CommonUtil.changeToNull(user);
                rowList.add(user);
            }

            if (!rowList.isEmpty()) {
                List<User> add_rowList = new ArrayList<>();
                List<User> up_rowList = new ArrayList<>();
                for (User user : rowList) {
                    if (user.getUserId() == null) {
                        add_rowList.add(user);
                    }else{
                        up_rowList.add(user);
                    }
                }
                if(!CollectionUtils.isEmpty(add_rowList)){
                    this.saveBatch(add_rowList,5000);
                }
                add_rowList.addAll(up_rowList);

                List<UserTenant> userTenants = new ArrayList<>();
                List<UserRole> userRoles = new ArrayList<>();
                List<UserOrg> userOrgs = new ArrayList<>();
                List<UserGroup> userGroups = new ArrayList<>();

                for (User user : add_rowList) {
                    //新增与租户的关系
                    UserTenant u = new UserTenant();
                    u.setUserId(user.getUserId());
                    u.setUserType(user.getUserType());
                    userTenants.add(u);

                    //机构
                    if(!CollectionUtils.isEmpty(user.getOrgs())){
                        for(Org _org:user.getOrgs()){
                            UserOrg userOrg = new UserOrg();
                            userOrg.setUserId(user.getUserId());
                            userOrg.setOrgId(_org.getOrgId());
                            userOrgs.add(userOrg);
                        }
                    }

                    //角色
                    if(!CollectionUtils.isEmpty(user.getRoles())){
                        for(Role _role:user.getRoles()){
                            UserRole userRole = new UserRole();
                            userRole.setUserId(user.getUserId());
                            userRole.setRoleId(_role.getRoleId());
                            userRoles.add(userRole);
                        }
                    }

                    //分组
                    if(!CollectionUtils.isEmpty(user.getGroups())){
                        for(Group _group:user.getGroups()){
                            UserGroup userGroup = new UserGroup();
                            userGroup.setUserId(user.getUserId());
                            userGroup.setGroupId(_group.getGroupId());
                            userGroups.add(userGroup);
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(userTenants)){
                    userTenantService.saveBatch(userTenants,5000);
                }
                if(!CollectionUtils.isEmpty(userRoles)){
                    userRoleService.saveBatch(userRoles,5000);
                }
                if(!CollectionUtils.isEmpty(userOrgs)){
                    userOrgService.saveBatch(userOrgs,5000);
                }
                if(!CollectionUtils.isEmpty(userGroups)){
                    userGroupService.saveBatch(userGroups,5000);
                }
            }

            int num = list.size() - 3 - rowList.size();
            if (num > 0) {
                excellReader.setCellValue(sheetIndex, 2, lastIndex, "错误信息", style_1);
                FileRecord upload = fileDfsUtil.upload("用户导入_错误数据.xlsx", excellReader.getByteArray(), ThreadLocalContextHolder.getUserId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",null);
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
            ExcelOperation entity = new ExcelOperation("/user_template.xlsx");

            DictionaryDataParam param = new DictionaryDataParam();
            param.setDictCode("user_type");
            List<DictionaryData> dictionaryDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(dictionaryDatas)){
                String[] userTypeArr = new String[dictionaryDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : dictionaryDatas) {
                    userTypeArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(3, userTypeArr,3,null,false,0);
            }

            param = new DictionaryDataParam();
            param.setDictCode("nation");
            dictionaryDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(dictionaryDatas)){
                String[] typeArr = new String[dictionaryDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : dictionaryDatas) {
                    typeArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(9, typeArr,3,null,false,0);
            }

            param = new DictionaryDataParam();
            param.setDictCode("politics");
            dictionaryDatas = dictionaryDataService.listRel(param);
            if(!CollectionUtils.isEmpty(dictionaryDatas)){
                String[] typeArr = new String[dictionaryDatas.size()];
                int index = 0;
                for (DictionaryData dictionaryData : dictionaryDatas) {
                    typeArr[index] = dictionaryData.getDictDataName();
                    index++;
                }
                entity.generateRangeList(10, typeArr,3,null,false,0);
            }

            List<Role> roleList = roleService.list(new LambdaQueryWrapper<Role>().select(Role::getRoleName));
            if(!CollectionUtils.isEmpty(roleList)){
                String[] roleArr = new String[roleList.size()];
                int index = 0;
                for (Role role : roleList) {
                    roleArr[index] = role.getRoleName();
                    index++;
                }
                entity.generateRangeList(13, roleArr,3,null,false,0);
            }


            List<Org> orgList = orgService.list(new LambdaQueryWrapper<Org>().select(Org::getOrgFullName));
            if(!CollectionUtils.isEmpty(orgList)){
                String[] orgArr = new String[orgList.size()];
                int index = 0;
                for (Org org : orgList) {
                    orgArr[index] = org.getOrgFullName();
                    index++;
                }
                entity.generateRangeList(14, orgArr,3,null,false,0);
            }

            List<Group> groupList = groupService.list(new LambdaQueryWrapper<Group>().select(Group::getGroupName));
            if(!CollectionUtils.isEmpty(groupList)){
                String[] groupArr = new String[groupList.size()];
                int index = 0;
                for (Group group : groupList) {
                    groupArr[index] = group.getGroupName();
                    index++;
                }
                entity.generateRangeList(15, groupArr,3,null,false,0);
            }

            entity.print(res, "用户导入模板".concat(ExcelConstant.EXCEL.COLLEGE_EXCEL_FINAL_POSION.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("服务器错误，请联系管理员！");
        }
    }

    /**
     * 批量查询用户其他信息
     *
     * @param users 用户集合
     */
    private void selectUserOthers(List<User> users) {
        if (!CollectionUtils.isEmpty(users)) {
            List<Integer> userIds = new ArrayList<>(users.size());
            for (User user : users) {
                userIds.add(user.getUserId());
            }
            List<Org> userOrgs = userOrgService.listByUserIds(userIds);
            Map<Integer,List<Org>> orgMap = new HashMap<>(userOrgs.size());
            if (!CollectionUtils.isEmpty(userOrgs)) {
                orgMap = userOrgs.stream().collect(Collectors.groupingBy(it -> it.getUserId()));
            }
            List<Role> userRoles = userRoleService.listByUserIds(userIds);
            Map<Integer,List<Role>> roleMap = new HashMap<>(userRoles.size());
            if (!CollectionUtils.isEmpty(userRoles)){
                roleMap = userRoles.stream().collect(Collectors.groupingBy(it -> it.getUserId()));
            }
            List<Group> userGroups = userGroupService.listByUserIds(userIds);
            Map<Integer,List<Group>> groupMap = new HashMap<>(userGroups.size());
            if (!CollectionUtils.isEmpty(userGroups)){
                groupMap = userGroups.stream().collect(Collectors.groupingBy(it -> it.getUserId()));
            }

            for (User user : users) {
                user.setPassword(null);
                user.setOrgs(orgMap.get(user.getUserId()));
                user.setRoles(roleMap.get(user.getUserId()));
                user.setGroups(groupMap.get(user.getUserId()));
            }
        }
    }
}

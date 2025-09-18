package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.mapper.DefaultRoleMapper;
import com.struggle.common.system.mapper.TenantMapper;
import com.struggle.common.system.mapper.TenantTableMapper;
import com.struggle.common.system.param.DefaultRoleParam;
import com.struggle.common.system.service.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:11
 */
@Service
public class DefaultRoleServiceImpl extends ServiceImpl<DefaultRoleMapper, DefaultRole> implements DefaultRoleService {
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private DefaultMenuService defaultMenuService;
    @Resource
    private DefaultRoleMenuService defaultRoleMenuService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserTenantService userTenantService;
    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private OrgService orgService;
    @Resource
    private TenantTableMapper tenantTableMapper;

    @Override
    public void getRoleChildrenIds(DefaultRole role, List<Integer> childrenIds) {
        List<DefaultRole> roles = baseMapper.selectList(Wrappers.<DefaultRole>lambdaQuery().select(DefaultRole::getRoleId).eq(DefaultRole::getParentId, role.getRoleId()));
        if(!CollectionUtils.isEmpty(roles)){
            for(DefaultRole _role : roles){
                childrenIds.add(_role.getRoleId());
                this.getRoleChildrenIds(_role,childrenIds);
            }
        }
    }

    @Override
    public List<DefaultRole> listRel(DefaultRoleParam param) {
        PageParam<DefaultRole, DefaultRoleParam> page = new PageParam<>(param);
        return  baseMapper.selectList(page.getOrderWrapper());
    }

    @Override
    public boolean saveRole(DefaultRole role) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<DefaultRole>().eq(DefaultRole::getRoleCode, role.getRoleCode()).eq(DefaultRole::getTenantType,role.getTenantType())) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        if (baseMapper.selectCount(new LambdaQueryWrapper<DefaultRole>().eq(DefaultRole::getRoleName, role.getRoleName()).eq(DefaultRole::getTenantType,role.getTenantType())) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        return baseMapper.insert(role) > 0;
    }

    @Override
    public boolean updateRole(DefaultRole role) {

        if (role.getRoleCode() != null && baseMapper.selectCount(new LambdaQueryWrapper<DefaultRole>().eq(DefaultRole::getRoleCode, role.getRoleCode()).eq(DefaultRole::getTenantType,role.getTenantType()).ne(DefaultRole::getRoleId, role.getRoleId())) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        if (role.getRoleName() != null && baseMapper.selectCount(new LambdaQueryWrapper<DefaultRole>().eq(DefaultRole::getRoleName, role.getRoleName()).eq(DefaultRole::getTenantType,role.getTenantType()).ne(DefaultRole::getRoleId, role.getRoleId())) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        if(role.getParentId() !=null && role.getParentId() !=0){
            if(role.getRoleId().equals(role.getParentId())){
                throw new BusinessException("上级角色不能是当前角色");
            }
            List<Integer> childrenIds = new ArrayList<>();
            this.getRoleChildrenIds(role,childrenIds);
            if(childrenIds.contains(role.getParentId())){
                throw new BusinessException("上级角色不能是当前角色的子角色");
            }
        }
        return baseMapper.update(role,Wrappers.<DefaultRole>lambdaUpdate()
                .set(DefaultRole::getComments,role.getComments())
                .eq(DefaultRole::getRoleId,role.getRoleId()))>0;
    }

    @Async("myAsyncExecutor")
    @Override
    public void initTenant(List<Tenant> tenantList) {
        if(!CollectionUtils.isEmpty(tenantList)){
            Map<String,List<Tenant>> tMap = new HashMap<>();
            //按照租户类型分组
            for(Tenant tenant:tenantList){
                List<Tenant> tenants = tMap.get(tenant.getTenantType());
                if(!CollectionUtils.isEmpty(tenants)){
                    tenants.add(tenant);
                }else{
                    tenants = new ArrayList<>();
                    tenants.add(tenant);
                    tMap.put(tenant.getTenantType(),tenants);
                }
            }
            List<TenantTable> tenantTables = tenantTableMapper._selectList();
            for (Map.Entry<String,List<Tenant>> entry : tMap.entrySet()) {
                List<DefaultRole> defaultRoles = new ArrayList<>();
                List<DefaultRoleMenu> roleMenuList = new ArrayList<>();
                List<DefaultMenu> defaultMenus = new ArrayList<>();
                //获取同步数据
                this.initParameter(entry.getKey(),defaultRoles,roleMenuList,defaultMenus);
                if(!CollectionUtils.isEmpty(defaultRoles)){
                    for(Tenant tenant:entry.getValue()){
                        try {
                            ThreadLocalContextHolder.setTenant(tenant.getTenantId());
                            this.initTenant(defaultRoles,roleMenuList,defaultMenus,tenant,tenantTables);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            ThreadLocalContextHolder.remove();
                        }
                    }
                }
            }
        }
    }

    @Async("myAsyncExecutor")
    @Override
    public void updateTenant(String tenantType) {
        List<Tenant> tenantList = tenantMapper.selectList(Wrappers.<Tenant>lambdaQuery().eq(Tenant::getTenantType, tenantType));
        if(!CollectionUtils.isEmpty(tenantList)) {
            List<DefaultRole> defaultRoles = new ArrayList<>();
            List<DefaultRoleMenu> roleMenuList = new ArrayList<>();
            List<DefaultMenu> defaultMenus = new ArrayList<>();
            //获取同步数据
            this.initParameter(tenantType,defaultRoles,roleMenuList,defaultMenus);
            for(Tenant tenant:tenantList){
                try{
                    ThreadLocalContextHolder.setTenant(tenant.getTenantId());
                    this.updateTenant(defaultRoles,roleMenuList,defaultMenus);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    ThreadLocalContextHolder.remove();
                }
            }
        }
    }

    @Transactional
    public void initTenant(List<DefaultRole> defaultRoles,List<DefaultRoleMenu> roleMenuList, List<DefaultMenu> defaultMenus,Tenant tenant,List<TenantTable> tenantTables){
        UserTenant userTenant = new UserTenant();
        userTenant.setUserId(configProperties.getDefaultUser());
        userTenantService.save(userTenant);

        Org org = new Org();
        org.setOrgType(configProperties.getDefaultOrgType());
        org.setOrgCode(configProperties.getDefaultOrgCode());
        org.setOrgName(tenant.getTenantName());
        org.setParentId(0);
        org.setComments("默认机构");
        org.setSortNumber(0);
        orgService.saveOrg(org);

        //创建租户表
        if(!CollectionUtils.isEmpty(tenantTables)){
            for(TenantTable tenantTable:tenantTables){
                String createSql = tenantTable.getCreateSql();
                createSql = createSql.replaceAll("\\{0\\}",tenant.getTenantId()+"");
                createSql = createSql.replaceAll("\\{1\\}",tenant.getTenantName());
                tenantTableMapper.execute(tenantTable.getDataSource(),createSql);
            }
        }

        if(!CollectionUtils.isEmpty(defaultRoles)) {
            Map<Integer, Integer> menuMap = new HashMap<>();
            Map<Integer, List<DefaultRoleMenu>> defaultRoleMenuMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(roleMenuList)) {
                for (DefaultRoleMenu defaultRoleMenu : roleMenuList) {
                    List<DefaultRoleMenu> defaultRoleMenus = defaultRoleMenuMap.get(defaultRoleMenu.getRoleId());
                    if (!CollectionUtils.isEmpty(defaultRoleMenus)) {
                        defaultRoleMenus.add(defaultRoleMenu);
                    } else {
                        defaultRoleMenus = new ArrayList<>();
                        defaultRoleMenus.add(defaultRoleMenu);
                        defaultRoleMenuMap.put(defaultRoleMenu.getRoleId(), defaultRoleMenus);
                    }
                }

                if (!CollectionUtils.isEmpty(defaultMenus)) {
                    this.initMenu(defaultMenus, menuMap, new HashMap<>(),new ArrayList<>());
                }
            }
            List<Integer> addRoleIdLists = new ArrayList<>();
            List<RoleMenu> roleMenus = new ArrayList<>();
            Map<Integer,Integer> roleMap = new HashMap<>();
            //初始化角色
            this.initRole(defaultRoles,roleMap,new HashMap<>(),new ArrayList<>(),addRoleIdLists);

            for(Map.Entry<Integer,Integer> entry : roleMap.entrySet()){
                Integer defaultRoleId = entry.getKey();
                Integer roleId = entry.getValue();
                List<DefaultRoleMenu> defaultRoleMenus = defaultRoleMenuMap.get(defaultRoleId);
                if (!CollectionUtils.isEmpty(defaultRoleMenus)) {
                    for (DefaultRoleMenu defaultRoleMenu : defaultRoleMenus) {
                        RoleMenu roleMenu = new RoleMenu();
                        roleMenu.setRoleId(roleId);
                        Integer menuId = menuMap.get(defaultRoleMenu.getMenuId());
                        if (menuId == null) {
                            continue;
                        }
                        roleMenu.setMenuId(menuId);
                        roleMenus.add(roleMenu);
                    }
                }
            }

            if(!CollectionUtils.isEmpty(roleMenus)){
                roleMenuService.saveBatch(roleMenus);
            }
            if(!CollectionUtils.isEmpty(addRoleIdLists)){
                userRoleService.saveBatch(configProperties.getDefaultUser(), addRoleIdLists);
            }
        }
    }

    private void initMenu(List<DefaultMenu> defaultMenus,Map<Integer,Integer> menuMap,Map<Integer,Menu> defaultMenuIdMap, List<Menu> updateMenus){
        for(DefaultMenu defaultMenu:defaultMenus){
            Menu menu = defaultMenuIdMap.get(defaultMenu.getMenuId());
            if(menu == null){
                menu = new Menu();
                menu.setAppType(defaultMenu.getAppType());
                menu.setMenuType(defaultMenu.getMenuType());
                menu.setMeta(defaultMenu.getMeta());
                menu.setAuthority(defaultMenu.getAuthority());
                menu.setHide(defaultMenu.getHide());
                menu.setSortNumber(defaultMenu.getSortNumber());
                menu.setComponent(defaultMenu.getComponent());
                menu.setPath(defaultMenu.getPath());
                menu.setTitle(defaultMenu.getTitle());
                menu.setIcon(defaultMenu.getIcon());
                menu.setSystemDefault(defaultMenu.getMenuId());
                if(defaultMenu.getParentId() ==null || defaultMenu.getParentId() == 0){
                    menu.setParentId(0);
                }else{
                    menu.setParentId(menuMap.get(defaultMenu.getParentId()));
                }
                menuService.save(menu);
            }else{
                menu.setAppType(defaultMenu.getAppType());
                menu.setMenuType(defaultMenu.getMenuType());
                menu.setMeta(defaultMenu.getMeta());
                menu.setAuthority(defaultMenu.getAuthority());
                menu.setHide(defaultMenu.getHide());
                menu.setSortNumber(defaultMenu.getSortNumber());
                menu.setComponent(defaultMenu.getComponent());
                menu.setPath(defaultMenu.getPath());
                menu.setTitle(defaultMenu.getTitle());
                menu.setIcon(defaultMenu.getIcon());
                menu.setSystemDefault(defaultMenu.getMenuId());
                if(defaultMenu.getParentId() ==null || defaultMenu.getParentId() == 0){
                    menu.setParentId(0);
                }else{
                    menu.setParentId(menuMap.get(defaultMenu.getParentId()));
                }

                updateMenus.add(menu);
                defaultMenuIdMap.remove(defaultMenu.getMenuId());
            }

            menuMap.put(defaultMenu.getMenuId(),menu.getMenuId());
            if(!CollectionUtils.isEmpty(defaultMenu.getChildren())){
                this.initMenu(defaultMenu.getChildren(),menuMap,defaultMenuIdMap,updateMenus);
            }
        }
    }

    private void initRole(List<DefaultRole> defaultRoles,Map<Integer,Integer> roleMap,Map<Integer,Role> defaultRoleIdMap, List<Role> updateRoles,List<Integer> addRoleIdLists){
        for(DefaultRole defaultRole:defaultRoles){
            Role role = defaultRoleIdMap.get(defaultRole.getRoleId());
            if(role == null){//需要新增
                role = new Role();
                role.setRoleCode(defaultRole.getRoleCode());
                role.setRoleName(defaultRole.getRoleName());
                role.setComments(defaultRole.getComments());
                if(defaultRole.getParentId() ==null || defaultRole.getParentId() == 0){
                    role.setParentId(0);
                }else{
                    role.setParentId(roleMap.get(defaultRole.getParentId()));
                }
                role.setSortNumber(defaultRole.getSortNumber());
                role.setScopeType(defaultRole.getScopeType());
                role.setSystemDefault(defaultRole.getRoleId());
                roleService.save(role);
                if(defaultRole.getRoleCode().equals(configProperties.getDefaultRoleCode())){
                    addRoleIdLists.add(role.getRoleId());
                }
            }else {//需要修改
                role.setRoleCode(defaultRole.getRoleCode());
                role.setRoleName(defaultRole.getRoleName());
                role.setComments(defaultRole.getComments());
                if(defaultRole.getParentId() ==null || defaultRole.getParentId() == 0){
                    role.setParentId(0);
                }else{
                    role.setParentId(roleMap.get(defaultRole.getParentId()));
                }
                role.setSortNumber(defaultRole.getSortNumber());
                role.setScopeType(defaultRole.getScopeType());
                role.setSystemDefault(defaultRole.getRoleId());

                updateRoles.add(role);
                defaultRoleIdMap.remove(defaultRole.getRoleId());
            }
            roleMap.put(defaultRole.getRoleId(),role.getRoleId());
            if(!CollectionUtils.isEmpty(defaultRole.getChildren())){
                this.initRole(defaultRole.getChildren(),roleMap,defaultRoleIdMap,updateRoles,addRoleIdLists);
            }
        }
    }

    @Transactional
    public void updateTenant(List<DefaultRole> defaultRoles,List<DefaultRoleMenu> roleMenuList, List<DefaultMenu> defaultMenus){
        //租户的对应角色
        List<Role> roleList = roleService.list(Wrappers.<Role>lambdaQuery().gt(Role::getSystemDefault, 0));
        //处理角色
        Map<Integer,Role> defaultRoleIdMap = new HashMap<>();
        List<Integer> roleIdList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roleList)){
            for(Role role:roleList){
                defaultRoleIdMap.put(role.getSystemDefault(),role);
                roleIdList.add(role.getRoleId());
            }
        }

        if(CollectionUtils.isEmpty(defaultRoles)){
            if(!CollectionUtils.isEmpty(roleIdList)){
                userRoleService.remove(Wrappers.<UserRole>lambdaQuery().in(UserRole::getRoleId,roleIdList));
                roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getRoleId,roleIdList));
                roleService.removeByIds(roleIdList);
                menuService.remove(Wrappers.<Menu>lambdaQuery().gt(Menu::getSystemDefault, 0));
            }
        }else{
            List<Integer> addRoleIdLists = new ArrayList<>();
            Map<Integer,Integer> roleMap = new HashMap<>();
            List<Role> updateRoles = new ArrayList<>();
            this.initRole(defaultRoles,roleMap,defaultRoleIdMap,updateRoles,addRoleIdLists);
            if(!CollectionUtils.isEmpty(updateRoles)){
                roleService.updateBatchById(updateRoles);
            }

            if(!CollectionUtils.isEmpty(addRoleIdLists)){
                userRoleService.saveBatch(configProperties.getDefaultUser(),addRoleIdLists);
            }

            if(!CollectionUtils.isEmpty(defaultRoleIdMap)){//要删除的角色
                List<Integer> removeRoles = new ArrayList<>();
                for (Map.Entry<Integer,Role> entry : defaultRoleIdMap.entrySet()) {
                    removeRoles.add(entry.getValue().getRoleId());
                }
                //删除用户角色关系
                userRoleService.remove(Wrappers.<UserRole>lambdaQuery().in(UserRole::getRoleId,removeRoles));
                //删除角色菜单
                roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getRoleId,removeRoles));
                //删除角色
                roleService.removeByIds(removeRoles);
            }

            //处理菜单
            Map<Integer,Menu> defaultMenuIdMap = new HashMap<>();
            Map<Integer, Integer> menuMap = new HashMap<>();
            List<Integer> menuIds = new ArrayList<>();
            List<Menu> _menuList = menuService.list(Wrappers.<Menu>lambdaQuery().gt(Menu::getSystemDefault, 0));
            if(!CollectionUtils.isEmpty(_menuList)){
                for(Menu menu:_menuList){
                    defaultMenuIdMap.put(menu.getSystemDefault(),menu);
                    menuIds.add(menu.getMenuId());
                }
            }

            if(CollectionUtils.isEmpty(defaultMenus)){
                if(!CollectionUtils.isEmpty(menuIds)){
                    menuService.remove(Wrappers.<Menu>lambdaQuery().in(Menu::getMenuId, menuIds));
                    roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getMenuId, menuIds));
                }
            }else{
                List<Menu> updateMenus =  new ArrayList<>();
                this.initMenu(defaultMenus,menuMap,defaultMenuIdMap,updateMenus);
                if(!CollectionUtils.isEmpty(updateMenus)){
                    menuService.updateBatchById(updateMenus,3000);
                }
                if(!CollectionUtils.isEmpty(defaultMenuIdMap)){//要删除的菜单
                    List<Integer> removeMenus = new ArrayList<>();
                    for (Map.Entry<Integer,Menu> entry : defaultMenuIdMap.entrySet()) {
                        removeMenus.add(entry.getValue().getMenuId());
                    }
                    menuService.removeByIds(removeMenus);
                    roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getMenuId,removeMenus));
                }

                //处理角色与菜单关系
                Map<String,RoleMenu> roleMenuMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(roleIdList)){
                    List<RoleMenu> list = roleMenuService.list(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getRoleId,roleIdList).in(RoleMenu::getMenuId,menuIds));
                    if(!CollectionUtils.isEmpty(list)){
                        for(RoleMenu roleMenu:list){
                            roleMenuMap.put(CommonUtil.stringJoiner(roleMenu.getRoleId().toString(),roleMenu.getMenuId().toString()),roleMenu);
                        }
                    }
                }
                List<RoleMenu> addRoleMenus = new ArrayList<>();
                for(DefaultRoleMenu roleMenu: roleMenuList){
                    Integer roleId = roleMap.get(roleMenu.getRoleId());
                    Integer menuId = menuMap.get(roleMenu.getMenuId());
                    if(roleId!= null && menuId !=null){
                        String key = CommonUtil.stringJoiner(roleId.toString(), menuId.toString());
                        RoleMenu roleMenu1 = roleMenuMap.get(key);
                        if(roleMenu1 == null){
                            RoleMenu _roleMenu = new RoleMenu();
                            _roleMenu.setRoleId(roleId);
                            _roleMenu.setMenuId(menuId);
                            addRoleMenus.add(_roleMenu);
                        }else{
                            roleMenuMap.remove(key);
                        }
                    }
                }

                if(!CollectionUtils.isEmpty(roleMenuMap)){//要删除的角色关系
                    List<Integer> removeRoleMenus = new ArrayList<>();
                    for (Map.Entry<String,RoleMenu> entry : roleMenuMap.entrySet()) {
                        removeRoleMenus.add(entry.getValue().getId());
                    }
                    //删除角色菜单
                    roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getId,removeRoleMenus));
                }

                if(!CollectionUtils.isEmpty(addRoleMenus)){//新增关系
                    roleMenuService.saveBatch(addRoleMenus);
                }
            }
        }
    }


    /**
     * 获取租户类型的角色、菜单、角色与菜单关系
     * @param tenantType
     * @param defaultRoles
     * @param roleMenuList
     * @param defaultMenus
     */
    private void initParameter(String tenantType,List<DefaultRole> defaultRoles,List<DefaultRoleMenu> roleMenuList, List<DefaultMenu> defaultMenus){
        List<DefaultRole> _defaultRoles = baseMapper.selectList(Wrappers.<DefaultRole>lambdaQuery().eq(DefaultRole::getTenantType, tenantType));
        if (!CollectionUtils.isEmpty(_defaultRoles)) {
            defaultRoles.addAll(CommonUtil.toTreeData(_defaultRoles, 0, DefaultRole::getParentId, DefaultRole::getRoleId, DefaultRole::setChildren));
            List<Integer> roleIds = new ArrayList<>(_defaultRoles.size());
            for (DefaultRole defaultRole : _defaultRoles) {
                roleIds.add(defaultRole.getRoleId());
            }
            roleMenuList.addAll(defaultRoleMenuService.list(Wrappers.<DefaultRoleMenu>lambdaQuery().in(DefaultRoleMenu::getRoleId, roleIds)));
            if (!CollectionUtils.isEmpty(roleMenuList)) {
                Set<Integer> menuIds = new HashSet<>();
                for (DefaultRoleMenu defaultRoleMenu : roleMenuList) {
                    menuIds.add(defaultRoleMenu.getMenuId());
                }
                List<DefaultMenu> menuList = defaultMenuService.list(Wrappers.<DefaultMenu>lambdaQuery().in(DefaultMenu::getMenuId, menuIds));
                if (!CollectionUtils.isEmpty(menuList)) {
                    defaultMenus.addAll(CommonUtil.toTreeData(menuList, 0, DefaultMenu::getParentId, DefaultMenu::getMenuId, DefaultMenu::setChildren));
                }
            }
        }
    }
}

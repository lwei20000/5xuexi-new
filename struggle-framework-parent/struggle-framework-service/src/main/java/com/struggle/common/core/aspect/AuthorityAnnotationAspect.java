package com.struggle.common.core.aspect;

import com.struggle.common.core.Constants;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.security.JwtUtil;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.AuthoritieMenu;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.service.OrgService;
import com.struggle.common.system.service.RoleMenuService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限校验
 *
 * @author EleAdmin
 * @since 2020-03-21 16:58:16:05
 */
@Aspect
@Component
public class AuthorityAnnotationAspect {

    @Resource
    private ConfigProperties configProperties;

    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private HttpServletRequest request;
    @Resource
    private OrgService orgService;

    @Pointcut("@annotation(com.struggle.common.core.annotation.AuthorityAnnotation)")
    public void authority() {
    }

    @Before(value = "authority() && @annotation(authorityAnnotation)")
    public void before(JoinPoint point, AuthorityAnnotation authorityAnnotation){
        User loginUser = ThreadLocalContextHolder.getUser();
        if(loginUser == null){
            throw new BusinessException(Constants.TOKEN_EXPIRED_CODE,Constants.TOKEN_EXPIRED_MSG);
        }
        String menuKey = CommonUtil.stringJoiner(configProperties.getMenuPrefix(),configProperties.getAppType(),JwtUtil.getAccessToken(request));
        Object menuJsons = redisUtil.get(menuKey);
        AuthoritieMenu authoritieMenu = new AuthoritieMenu();
        if(menuJsons != null){
            authoritieMenu =  JSONUtil.parseObject(menuJsons.toString(),AuthoritieMenu.class);
        }else{
            List<Role> roles = loginUser.getRoles();
            if(!CollectionUtils.isEmpty(roles)){
                List<Integer> roleIds = new ArrayList<>(roles.size());
                for (Role role:roles){
                    roleIds.add(role.getRoleId());
                }
                List<Menu> menuList = roleMenuService.listMenuByRoleIds(roleIds, null, configProperties.getAppType());
                authoritieMenu = new AuthoritieMenu(menuList);
                redisUtil.set(menuKey,JSONUtil.toJSONString(authoritieMenu),configProperties.getTokenExpireTime());
            }
        }

        Map<String,List<Integer>> authoritieMap = authoritieMenu.getAuthoritieMap();
        if(!CollectionUtils.isEmpty(authoritieMap)){
            String value = authorityAnnotation.value();
            List<Integer> roleIds = authoritieMap.get(value);
            if(CollectionUtils.isEmpty(roleIds)){
                throw new BusinessException(Constants.UNAUTHORIZED_CODE,Constants.UNAUTHORIZED_MSG);
            }
            if(authorityAnnotation.permission()){
                Map<Integer,Role> roleMap = new HashMap<>(16);
                List<Role> roles = loginUser.getRoles();
                if(!CollectionUtils.isEmpty(roles)) {
                    for (Role role : roles) {
                        roleMap.put(role.getRoleId(),role);
                    }
                }
                String permissionType = DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_1.getCode();
                Set<Role> permissionRoleList = new HashSet<>();
                for(Integer roleId :roleIds){
                    Role role = roleMap.get(roleId);
                    if(role != null){
                        if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_2.getCode())){
                            //全部权限
                            permissionType = DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_2.getCode();
                            break;
                        }else if(!role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_1.getCode()) && !role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_2.getCode())){
                            //机构权限
                            permissionType = DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_3.getCode();
                            permissionRoleList.add(role);
                        }
                    }
                }

                DataPermissionParam dataPermissionParam = new DataPermissionParam();
                //无数据权限，直接返回
                if(permissionType.equals(DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_1.getCode())){
                    if(StringUtils.hasText(authorityAnnotation.permissionErr()) && authorityAnnotation.permissionErr().indexOf(permissionType)>-1){
                        throw new BusinessException(Constants.UNAUTHORIZED_DATA_CODE,Constants.UNAUTHORIZED_DATA_MSG);
                    }
                }else{
                    Set<Integer> fullList = new HashSet<>();
                    Set<Integer> list = new HashSet<>();
                    if(permissionType.equals(DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_3.getCode())){
                        for(Role role : permissionRoleList){
                            List<Org> orgs = role.getOrgs();
                            if(!CollectionUtils.isEmpty(orgs)){
                                if(role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_4.getCode() )||
                                        role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_6.getCode() )||
                                        role.getScopeType().equals(Constants.SCOPE_TYPE.SCOPE_TYPE_8.getCode() )){
                                    fullList.addAll(orgs.stream().map(Org::getOrgId).collect(Collectors.toList()));
                                }else{
                                    list.addAll(orgs.stream().map(Org::getOrgId).collect(Collectors.toList()));
                                }
                            }
                        }

                        if(!CollectionUtils.isEmpty(fullList) || !CollectionUtils.isEmpty(list)){
                            if(!CollectionUtils.isEmpty(fullList)){//查询子集机构
                                list.addAll(orgService.getChildrens(new ArrayList<>(fullList)));
                            }
                            dataPermissionParam.setOrgIds(new ArrayList<>(list));
                        }else{
                            //没有管理的机构
                            permissionType = DataPermissionParam.PERMISSION_TYPE.PERMISSION_TYPE_4.getCode();
                            if(StringUtils.hasText(authorityAnnotation.permissionErr()) && authorityAnnotation.permissionErr().indexOf(permissionType)>-1){
                                throw new BusinessException(Constants.UNAUTHORIZED_DATA_CODE,Constants.UNAUTHORIZED_DATA_MSG);
                            }
                        }
                    }
                }
                dataPermissionParam.setPermissionType(permissionType);
                ThreadLocalContextHolder.setPermission(dataPermissionParam);
            }
        }else{
            throw new BusinessException(Constants.UNAUTHORIZED_CODE,Constants.UNAUTHORIZED_MSG);
        }
    }
}

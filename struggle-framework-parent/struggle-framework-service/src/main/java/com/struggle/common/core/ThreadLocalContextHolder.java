package com.struggle.common.core;

import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.system.entity.User;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalContextHolder {

    private static final String TENANT_ID = "tenantId";
    private static final String LOGIN_INFO = "loginInfo";
    private static final String PERMISSION = "permission";
    private static final String CLOSE_TENANT = "closeTenant";
    /**
     * 本地线程对象
     */
    static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<>();


    public static void remove() {
        threadLocal.remove();
    }


    public static void removeUser() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.remove(LOGIN_INFO);
        }
    }

    public static void setUser(User user) {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.put(LOGIN_INFO,user);
        }else{
            _map = new HashMap<>();
            _map.put(LOGIN_INFO,user);
            threadLocal.set(_map);
        }
    }

    public static User getUser() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            Object o = _map.getOrDefault(LOGIN_INFO,null);
            return o == null ? null : (User)o;
        }
        return null;
    }

    public static Integer getUserId() {
        User user = getUser();
        if(user !=null){
            return user.getUserId();
        }
        return null;
    }

    public static void removeTenant() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.remove(TENANT_ID);
        }
    }

    public static void setCloseTenant(boolean close) {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.put(CLOSE_TENANT,close);
        }else{
            _map = new HashMap<>();
            _map.put(CLOSE_TENANT,close);
            threadLocal.set(_map);
        }
    }

    public static boolean getCloseTenant() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            Object o = _map.getOrDefault(CLOSE_TENANT,null);
            return o == null ? false : (boolean)o;
        }
        return false;
    }


    public static void removeCloseTenant() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.remove(CLOSE_TENANT);
        }
    }

    public static void setTenant(Integer tenantId) {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.put(TENANT_ID,tenantId);
        }else{
            _map = new HashMap<>();
            _map.put(TENANT_ID,tenantId);
            threadLocal.set(_map);
        }
    }

    public static Integer getTenant() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            Object o = _map.getOrDefault(TENANT_ID,null);
            return o == null ? null : (Integer)o;
        }
        return null;
    }

    public static void removePermission() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.remove(PERMISSION);
        }
    }

    public static void setPermission(DataPermissionParam dataPermissionParam) {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            _map.put(PERMISSION,dataPermissionParam);
        }else{
            _map = new HashMap<>();
            _map.put(PERMISSION,dataPermissionParam);
            threadLocal.set(_map);
        }
    }

    public static DataPermissionParam getPermission() {
        Map<String, Object> _map = threadLocal.get();
        if(!CollectionUtils.isEmpty(_map)){
            Object o = _map.getOrDefault(PERMISSION,null);
            return o == null ? null : (DataPermissionParam)o;
        }
        return null;
    }
}

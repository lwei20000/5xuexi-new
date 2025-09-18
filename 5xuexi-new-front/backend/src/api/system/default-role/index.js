import request from '@/utils/request';

/**
 * 分页查询角色
 * @param params 查询条件
 */
export async function pageDefaultRoles(params) {
  const res = await request.get('/system/default-role/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询角色列表
 * @param params 查询条件
 */
export async function listDefaultRoles(params) {
  const res = await request.get('/system/default-role', {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.length>0){
      for(var i=0;i<data.length;i++){
        data[i].name =  data[i].tenantType+"_"+ data[i].roleName;
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加角色
 * @param data 角色信息
 */
export async function addDefaultRole(data) {
  const res = await request.post('/system/default-role', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改角色
 * @param data 角色信息
 */
export async function updateDefaultRole(data) {
  const res = await request.post('/system/default-role/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除角色
 * @param id 角色id
 */
export async function removeDefaultRole(id) {
  const res = await request.post('/system/default-role/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 同步资源
 * @param 租户类型
 */
export async function syncDefaultRole(tenantType) {
  const res = await request.get('/system/default-role/sync/' + tenantType);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除角色
 * @param ids 角色id集合
 */
export async function removeDefaultRoles(data) {
  const res = await request.post('/system/default-role/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取角色分配的菜单
 * @param roleId 角色id
 */
export async function listDefaultRoleMenus(roleId,appType) {
  const res = await request.get('/system/default-role-menu/'+appType+'/' + roleId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改角色菜单
 * @param roleId 角色id
 * @param data 菜单id集合
 */
export async function updateDefaultRoleMenus(roleId,appType, data) {
  const res = await request.post('/system/default-role-menu/'+appType+'/' + roleId, data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

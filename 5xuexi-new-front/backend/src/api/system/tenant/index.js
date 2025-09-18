import request from '@/utils/request';

/**
 * 分页查询租户
 * @param params 查询条件
 */
export async function pageTenants(params) {
  const res = await request.get('/system/tenant/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询租户列表
 * @param params 查询条件
 */
export async function listTenants(params) {
  const res = await request.get('/system/tenant', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取租户信息
 */
export async function getTenant(tenantId) {
  const res = await request.get('/system/tenant/'+tenantId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加租户
 * @param data 租户信息
 */
export async function addTenant(data) {
  const res = await request.post('/system/tenant', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改租户
 * @param data 租户信息
 */
export async function updateTenant(data) {
  const res = await request.post('/system/tenant/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除租户
 * @param id 租户id
 */
export async function removeTenant(id) {
  const res = await request.post('/system/tenant/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导入用户
 * @param file excel文件
 */
export async function importExcel(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/system/tenant/import', formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载导入模板
 */
export async function downloadTemplate() {
  const res = await request.get('/system/tenant/templateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

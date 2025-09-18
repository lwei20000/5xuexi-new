import request from '@/utils/request';

/**
 * 分页查询机构
 * @param params 查询条件
 */
export async function pageOrgs(params) {
  const res = await request.get('/system/org/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询机构列表
 * @param params 查询条件
 */
export async function listOrgs(params) {
  const res = await request.get('/system/org', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加机构
 * @param data 机构信息
 */
export async function addOrg(data) {
  const res = await request.post('/system/org', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改机构
 * @param data 机构信息
 */
export async function updateOrg(data) {
  const res = await request.post('/system/org/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除机构
 * @param id 机构id
 */
export async function removeOrg(id) {
  const res = await request.post('/system/org/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

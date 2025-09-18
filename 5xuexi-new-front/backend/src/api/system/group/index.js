import request from '@/utils/request';

/**
 * 分页查询分组
 * @param params 查询条件
 */
export async function pageGroups(params) {
  const res = await request.get('/system/group/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询分组列表
 * @param params 查询条件
 */
export async function listGroups(params) {
  const res = await request.get('/system/group', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加分组
 * @param data 分组信息
 */
export async function addGroup(data) {
  const res = await request.post('/system/group', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改分组
 * @param data 分组信息
 */
export async function updateGroup(data) {
  const res = await request.post('/system/group/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除分组
 * @param id 分组id
 */
export async function removeGroup(id) {
  const res = await request.post('/system/group/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除分组
 * @param ids 分组id集合
 */
export async function removeGroups(data) {
  const res = await request.post('/system/group/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

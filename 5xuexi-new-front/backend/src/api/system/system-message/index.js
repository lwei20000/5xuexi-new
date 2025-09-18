import request from '@/utils/request';

/**
 * 管理端-分页查询SystemMessage
 * @param params 查询条件
 */
export async function pageSystemMessages(params) {
  const res = await request.get('/system/system_message/admin/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-获取系统消息
 */
export async function getSystemMessage(id) {
  const res = await request.get('/system/system_message/detail/'+id);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-分页查询SystemMessageUser
 * @param params 查询条件
 */
export async function pageSystemMessageUsers(params) {
  const res = await request.get('/system/system_message/read_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加SystemMessage
 * @param data SystemMessage信息
 */
export async function addSystemMessage(data) {
  const res = await request.post('/system/system_message/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改SystemMessage
 * @param data SystemMessage信息
 */
export async function updateSystemMessage(data) {
  const res = await request.post('/system/system_message/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除SystemMessage
 * @param id SystemMessage
 */
export async function removeSystemMessages(data) {
  const res = await request.post('/system/system_message/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-分页查询SystemMessage
 * @param params 查询条件
 */
export async function _pageSystemMessages(params) {
  const res = await request.get('/system/system_message/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-SystemMessage详情
 * @param params 查询条件
 */
export async function _DetailSystemMessage(params) {
  const res = await request.get('/system/system_message/'+params);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}



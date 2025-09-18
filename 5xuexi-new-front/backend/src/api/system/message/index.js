import request from '@/utils/request';

/**
 * 管理端-分页查询Message
 * @param params 查询条件
 */
export async function pageMessages(params) {
  const res = await request.get('/system/message/admin/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-获取消息
 */
export async function getMessage(id) {
  const res = await request.get('/system/message/detail/'+id);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-分页查询MessageUser
 * @param params 查询条件
 */
export async function pageMessageUsers(params) {
  const res = await request.get('/system/message/read_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加Message
 * @param data Message信息
 */
export async function addMessage(data) {
  const res = await request.post('/system/message/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改Message
 * @param data Message信息
 */
export async function updateMessage(data) {
  const res = await request.post('/system/message/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除Message
 * @param id Messageid
 */
export async function removeMessages(data) {
  const res = await request.post('/system/message/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-分页查询Message
 * @param params 查询条件
 */
export async function _pageMessages(params) {
  const res = await request.get('/system/message/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-Message详情
 * @param params 查询条件
 */
export async function _DetailMessage(params) {
  const res = await request.get('/system/message/'+params);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}



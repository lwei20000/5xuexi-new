import request from '@/utils/request';

/**
 * 管理端-分页查询Feedback
 * @param params 查询条件
 */
export async function pageFeedbacks(params) {
  const res = await request.get('/system/sysFeedback/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改Feedback
 * @param data sysFeedback信息
 */
export async function updateFeedback(data) {
  const res = await request.post('/system/sysFeedback/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除sysFeedback
 * @param id Feedbackid
 */
export async function removeFeedbacks(data) {
  const res = await request.post('/system/sysFeedback/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 用户端-分页查询Feedback
 * @param params 查询条件
 */
export async function pageUserFeedbacks(params) {
  const res = await request.get('/system/sysFeedback/user_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-添加sysFeedback
 * @param data Feedback信息
 */
export async function addUserFeedback(data) {
  const res = await request.post('/system/sysFeedback/user_save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-修改Feedback
 * @param data sysFeedback信息
 */
export async function updateUserFeedback(data) {
  const res = await request.post('/system/sysFeedback/user_update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-删除sysFeedback
 * @param id Feedbackid
 */
export async function removeUserFeedbacks(data) {
  const res = await request.post('/system/sysFeedback/user_batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-修改状态
 * @param userId 用户id
 */
export async function updateUserStatus(id,status) {
  const res = await request.get('/system/sysFeedback/user_update_status?id='+id+"&status="+status);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}



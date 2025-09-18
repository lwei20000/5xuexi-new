import request from '@/utils/request';

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



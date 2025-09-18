import request from '@/utils/request';

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



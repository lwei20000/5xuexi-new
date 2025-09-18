import request from '@/utils/request';
/**
 * 用户端-分页查询SystemAnnouncement
 * @param params 查询条件
 */
export async function _pageSystemAnnouncements(params) {
  const res = await request.get('/system/system_announcement/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-SystemAnnouncement详情
 * @param params 查询条件
 */
export async function _DetailSystemAnnouncement(params) {
  const res = await request.get('/system/system_announcement/'+params);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}



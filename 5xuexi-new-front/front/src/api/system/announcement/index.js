import request from '@/utils/request';

/**
 * 用户端-分页查询Announcement
 * @param params 查询条件
 */
export async function _pageAnnouncements(params) {
  const res = await request.get('/system/announcement/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-Announcement详情
 * @param params 查询条件
 */
export async function _DetailAnnouncement(params) {
  const res = await request.get('/system/announcement/'+params);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}



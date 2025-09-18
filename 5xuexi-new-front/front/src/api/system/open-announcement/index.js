import request from '@/utils/request';
/**
 * 用户端-分页查询OpenAnnouncement
 * @param params 查询条件
 */
export async function _pageOpenAnnouncements(params) {
  const res = await request.get('/system/open-announcement/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 用户端-OpenAnnouncement详情
 * @param params 查询条件
 */
export async function _DetailOpenAnnouncement(openAnnouncementId) {
  const res = await request.get('/system/open-announcement/'+openAnnouncementId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}



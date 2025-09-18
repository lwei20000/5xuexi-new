import request from '@/utils/request';

/**
 * 管理端-分页查询OpenAnnouncement
 * @param params 查询条件
 */
export async function pageOpenAnnouncements(params) {
  const res = await request.get('/system/open-announcement/admin/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-获取公告
 */
export async function getOpenAnnouncement(id) {
  const res = await request.get('/system/open-announcement/detail/'+id);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改置顶状态
 * @param userId 用户id
 * @param status 状态
 */
export async function updateTopTimestamp(openAnnouncementId, topTimestamp) {
  const res = await request.post('/system/open-announcement/topTimestamp', {
    openAnnouncementId,
    topTimestamp
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加announcement
 * @param data OpenAnnouncement信息
 */
export async function addOpenAnnouncement(data) {
  const res = await request.post('/system/open-announcement/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改OpenAnnouncement
 * @param data announcement信息
 */
export async function updateOpenAnnouncement(data) {
  const res = await request.post('/system/open-announcement/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除announcement
 * @param id OpenAnnouncementid
 */
export async function removeOpenAnnouncements(data) {
  const res = await request.post('/system/open-announcement/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

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



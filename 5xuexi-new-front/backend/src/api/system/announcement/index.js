import request from '@/utils/request';

/**
 * 管理端-分页查询Announcement
 * @param params 查询条件
 */
export async function pageAnnouncements(params) {
  const res = await request.get('/system/announcement/admin/page', {
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
export async function getAnnouncement(id) {
  const res = await request.get('/system/announcement/detail/'+id);
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
export async function updateTopTimestamp(announcementId, topTimestamp) {
  const res = await request.post('/system/announcement/topTimestamp', {
    announcementId,
    topTimestamp
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-分页查询AnnouncementUser
 * @param params 查询条件
 */
export async function pageAnnouncementUsers(params) {
  const res = await request.get('/system/announcement/read_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加announcement
 * @param data Announcement信息
 */
export async function addAnnouncement(data) {
  const res = await request.post('/system/announcement/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改Announcement
 * @param data announcement信息
 */
export async function updateAnnouncement(data) {
  const res = await request.post('/system/announcement/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除announcement
 * @param id Announcementid
 */
export async function removeAnnouncements(data) {
  const res = await request.post('/system/announcement/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

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



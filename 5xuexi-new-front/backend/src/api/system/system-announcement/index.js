import request from '@/utils/request';

/**
 * 管理端-分页查询SystemAnnouncement
 * @param params 查询条件
 */
export async function pageSystemAnnouncements(params) {
  const res = await request.get('/system/system_announcement/admin/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-获取系统公告
 */
export async function getSystemAnnouncement(id) {
  const res = await request.get('/system/system_announcement/detail/'+id);
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
export async function updateTopTimestamp(systemAnnouncementId, topTimestamp) {
  const res = await request.post('/system/system_announcement/topTimestamp', {
    systemAnnouncementId,
    topTimestamp
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-分页查询SystemAnnouncementUser
 * @param params 查询条件
 */
export async function pageSystemAnnouncementUsers(params) {
  const res = await request.get('/system/system_announcement/read_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加announcement
 * @param data SystemAnnouncement信息
 */
export async function addSystemAnnouncement(data) {
  const res = await request.post('/system/system_announcement/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-修改SystemAnnouncement
 * @param data SystemAnnouncement
 */
export async function updateSystemAnnouncement(data) {
  const res = await request.post('/system/system_announcement/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除announcement
 * @param id SystemAnnouncement
 */
export async function removeSystemAnnouncements(data) {
  const res = await request.post('/system/system_announcement/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

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



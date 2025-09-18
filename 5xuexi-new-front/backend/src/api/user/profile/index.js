import request from '@/utils/request';

/**
 * 修改用户
 * @param data 用户信息
 */
export async function updateUser(data) {
  const res = await request.post('/auth/user', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 安全问题添加和修改
 * @param data 用户信息
 */
export async function settingQa(data) {
  const res = await request.post('/security/sysUserProblem/saveOrUpdate', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 获取当前用户安全问题
 * @param data 用户信息
 */
export async function getUserQa() {
  const res = await request.get('/security/sysUserProblem/listByLoginName');
  if (res.data.code === 0) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取用户名安全问题
 * @param data 用户名
 */
export async function getUserNotQa(params) {
  const res = await request.get('/security/sysUserProblem/not_listByLoginName', {params});
  if (res.data.code === 0) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 解绑用户安全问题
 * @param data 用户信息
 */
export async function unbindQa(data) {
  const res = await request.post('/security/sysUserProblem/unbinding', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

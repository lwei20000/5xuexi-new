import request from '@/utils/request';

/**
 * 分页查询用户专业
 * @param params 查询条件
 */
export async function pageUserMajors(params) {
  const res = await request.get('/base/user-major/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询用户专业列表
 * @param params 查询条件
 */
export async function listUserMajors(params) {
  const res = await request.get('/base/user-major', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加用户专业
 * @param data 用户专业信息
 */
export async function addUserMajor(data) {
  const res = await request.post('/base/user-major', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改用户专业
 * @param data 用户专业信息
 */
export async function updateUserMajor(data) {
  const res = await request.post('/base/user-major/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除用户专业
 * @param id 用户专业id
 */
export async function removeUserMajor(id) {
  const res = await request.post('/base/user-major/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除用户专业
 * @param ids 用户专业id集合
 */
export async function removeUserMajors(data) {
  const res = await request.post('/base/user-major/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 导入租户
 * @param file excel文件
 */
export async function importExcel(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/base/user-major/import', formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载导入模板
 */
export async function downloadTemplate() {
  const res = await request.get('/base/user-major/templateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导入租户
 * @param file excel文件
 */
export async function thesisImport(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/base/user-major/thesisImport', formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载导入模板
 */
export async function thesisTemplateExport() {
  const res = await request.get('/base/user-major/thesisTemplateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量下载登记表
 * @param {Array<number>} userMajorIds 用户专业ID列表
 */
export async function downloadRegistration(userMajorIds) {
  const res = await request.post('/base/user-major/downloadRegister', { userMajorIds }, { responseType: 'blob' });
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量下载学籍表
 * @param {Array<number>} userMajorIds 用户专业ID列表
 */
export async function downloadEnrollment(userMajorIds) {
  const res = await request.post('/base/user-major/downloadEnrollment', {userMajorIds}, {responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
}

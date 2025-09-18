import request from '@/utils/request';

/**
 * 分页查询用户课程
 * @param params 查询条件
 */
export async function pageUserCourses(params) {
  const res = await request.get('/base/user-course/page',{ params});
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * list查询用户课程
 * @param params 查询条件
 */
export async function listUserCourses(params) {
  const res = await request.post('/base/user-course/list', params);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导出用户课程
 * @param params 查询条件
 */
export async function doDownloadData(params) {
  const res = await request.get('/base/user-course/dataExport',{
    responseType: 'blob',
    params
  });
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * list更新用户课程成绩
 * @param params 查询条件
 */
export async function updateUserCoursesScore(params) {
  const res = await request.post('/base/user-course/updateScore', params);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * list更新用户课程成绩
 * @param params 查询条件
 */
export async function updateUserExamScore(params) {
  const res = await request.post('/base/user-course/update', params);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导入租户
 * @param file excel文件
 */
export async function scoreImport(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/base/user-course/scoreImport', formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载导入模板
 */
export async function scoreTemplateExport() {
  const res = await request.get('/base/user-course/scoreTemplateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}


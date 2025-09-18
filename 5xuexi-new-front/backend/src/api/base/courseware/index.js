import request from '@/utils/request';
/**
 * 查询章节课时列表
 * @param params 查询条件
 */
export async function listCoursewares(params) {
  const res = await request.get('/base/courseware', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加章节课时
 * @param data 章节课时信息
 */
export async function addCourseware(data) {
  const res = await request.post('/base/courseware', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 复制
 */
export async function copyCourseware(data) {
  const res = await request.post('/base/courseware/copy', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 修改章节课时
 * @param data 章节课时信息
 */
export async function updateCourseware(data) {
  const res = await request.post('/base/courseware/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除章节课时
 * @param id 章节课时id
 */
export async function removeCourseware(id) {
  const res = await request.post('/base/courseware/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

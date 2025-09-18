import request from '@/utils/request';

/**
 * 分页查询专业课程
 * @param params 查询条件
 */
export async function pagePublishMajorCourses(params) {
  const res = await request.get('/exam/tMajorCourseExam/major_course_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量发布
 * @param data
 */
export async function batchSave(data) {
  const res = await request.post('/exam/tMajorCourseExam/batch_save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 分页查询发布试卷
 * @param params 查询条件
 */
export async function pageMajorCourseExams(params) {
  const res = await request.get('/exam/tMajorCourseExam/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 添加
 * @param data
 */
export async function addMajorCourseExam(data) {
  const res = await request.post('/exam/tMajorCourseExam/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改
 * @param data
 */
export async function updateMajorCourseExam(data) {
  const res = await request.post('/exam/tMajorCourseExam/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除
 * @param id
 */
export async function removeMajorCourseExams(data) {
  const res = await request.post('/exam/tMajorCourseExam/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}



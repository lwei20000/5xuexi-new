import request from '@/utils/request';

/**
 * 分页查询考试
 * @param params 查询条件
 */
export async function pageUserMajorCourseExams(params) {
  const res = await request.get('/exam/tUserMajorCourseExam/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取试卷
 */
export async function getPaper(userMajorCourseExamId) {
  const res = await request.get('/exam/tUserMajorCourseExam/'+userMajorCourseExamId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批改试卷
 * @param params 查询条件
 */
export async function correctPaper(data) {
  const res = await request.post('/exam/tUserMajorCourseExam/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 退回作答
 */
export async function backPaper(userMajorCourseExamId) {
  const res = await request.get('/exam/tUserMajorCourseExam/back/'+userMajorCourseExamId);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 导出考试信息
 * @param params 查询条件
 */
export async function doDownloadData(params) {
  const res = await request.get('/exam/tUserMajorCourseExam/dataExport',{
    responseType: 'blob',
    params
  });
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}




import request from '@/utils/request';

/**
 * 获取专业
 */
export async function getMajor() {
  const res = await request.get('/front/studentMajor');
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取章节课时
 */

export async function getCourseware(majorId,courseId) {
  const res = await request.get('/front/courseware/'+majorId+'/'+courseId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 保存进度
 */
export async function saveLearn(data) {
  const res = await request.post('/front/userLearning', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取试卷
 */

export async function getPaper(majorId,courseId) {
  const res = await request.get('/front/exam/'+majorId+'/'+courseId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 提交试卷
 */
export async function submitPaper(data) {
  const res = await request.post('/front/submitPaper', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 下载导入模板
 */
export async function downloadTemplate1(id) {
  const res = await request.get('/front/download/register/'+id,{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 下载导入模板
 */
export async function downloadTemplate2(id) {
  const res = await request.get('/front/download/enrollment/'+id,{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 保存论文信息
 */
export async function saveThesis(data) {
  const res = await request.post('/front/updateThesis', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * list查询用户课程
 * @param params 查询条件
 */
export async function listUserCourses(params) {
  const res = await request.post('/front/listUserCourses', params);
  if (res.status === 200) {
    return {
      user: res.data.user,
      userMajor: res.data.userMajor,
      userCourseList: res.data.userCourseList,
    };
  }
  return Promise.reject(new Error(res.data.message));
}



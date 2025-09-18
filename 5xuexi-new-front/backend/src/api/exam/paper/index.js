import request from '@/utils/request';

/**
 * 分页查询课程
 * @param params 查询条件
 */
export async function pagePaperCourses(params) {
  const res = await request.get('/exam/tPaper/course_page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 分页查询试卷
 * @param params 查询条件
 */
export async function pagePapers(params) {
  const res = await request.get('/exam/tPaper/page', {
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
export async function addPaper(data) {
  const res = await request.post('/exam/tPaper/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改
 * @param data
 */
export async function updatePaper(data) {
  const res = await request.post('/exam/tPaper/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除
 * @param id
 */
export async function removePapers(data) {
  const res = await request.post('/exam/tPaper/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导入答题卡
 * @param file excel文件
 */
export async function importExcel(file,paperId) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/exam/tPaperQuestion/import?paperId='+paperId, formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载答题卡模板
 */
export async function downloadTemplate() {
  const res = await request.get('/exam/tPaperQuestion/templateExport',{responseType: 'blob'});
  debugger;
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 预览试卷
 */
export async function getPaper(paperId) {
  const res = await request.get('/exam/tPaper/'+paperId);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

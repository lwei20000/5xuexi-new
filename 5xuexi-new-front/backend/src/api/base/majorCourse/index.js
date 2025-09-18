import request from '@/utils/request';

/**
 * 分页查询专业课程
 * @param params 查询条件
 */
export async function pageMajorCourses(params) {
  const res = await request.get('/base/major-course/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询专业课程列表
 * @param params 查询条件
 */
export async function listMajorCourses(params) {
  const res = await request.get('/base/major-course', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加专业课程
 * @param data 专业课程信息
 */
export async function addMajorCourse(data) {
  const res = await request.post('/base/major-course', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 复制专业课程
 * @param data 专业课程信息
 */
export async function copyMajorCourse(data) {
  const res = await request.post('/base/major-course/copy', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 修改专业课程
 * @param data 专业课程信息
 */
export async function updateMajorCourse(data) {
  const res = await request.post('/base/major-course/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除专业课程
 * @param id 专业课程id
 */
export async function removeMajorCourse(id) {
  const res = await request.post('/base/major-course/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除专业课程
 * @param ids 专业课程id集合
 */
export async function removeMajorCourses(data) {
  const res = await request.post('/base/major-course/batch',data);
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
  const res = await request.post('/base/major-course/import', formData);
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
  const res = await request.get('/base/major-course/templateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

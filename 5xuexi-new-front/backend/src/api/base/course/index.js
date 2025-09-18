import request from '@/utils/request';

/**
 * 分页查询课程
 * @param params 查询条件
 */
export async function pageCourses(params) {
  const res = await request.get('/base/course/page', {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.list && data.list.length>0){
      for(var i=0;i<data.list.length;i++){
        data.list[i].name =  data.list[i].courseCode+"_"+ data.list[i].courseName;
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询课程列表
 * @param params 查询条件
 */
export async function listCourses(params) {
  const res = await request.get('/base/course', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加课程
 * @param data 课程信息
 */
export async function addCourse(data) {
  const res = await request.post('/base/course', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改课程
 * @param data 课程信息
 */
export async function updateCourse(data) {
  const res = await request.post('/base/course/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除课程
 * @param id 课程id
 */
export async function removeCourse(id) {
  const res = await request.post('/base/course/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除课程
 * @param ids 课程id集合
 */
export async function removeCourses(data) {
  const res = await request.post('/base/course/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改课程别名
 * @param data 课程信息
 */
export async function updateAlias(data) {
  const res = await request.post('/base/course/updateAlias', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

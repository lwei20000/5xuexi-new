import request from '@/utils/request';

/**
 * 用户查询文档列表
 * @param params 查询条件
 */
export async function listDocs(params) {
  const res = await request.get('/system/doc', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 管理员查询文档列表
 * @param params 查询条件
 */
export async function listAdminDocs(params) {
  const res = await request.get('/system/doc/admin', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加文档
 * @param data 文档信息
 */
export async function addDoc(data) {
  const res = await request.post('/system/doc', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改文档
 * @param data 文档信息
 */
export async function updateDoc(data) {
  const res = await request.post('/system/doc/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除文档
 * @param id 文档id
 */
export async function removeDoc(id) {
  const res = await request.post('/system/doc/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}




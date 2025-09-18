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



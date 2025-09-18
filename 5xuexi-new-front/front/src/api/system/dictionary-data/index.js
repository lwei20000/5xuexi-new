import request from '@/utils/request';

/**
 * 查询字典数据列表
 * @param params 查询条件
 */
export async function listDictionaryData(params) {
  const res = await request.get('/system/dictionary-data', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

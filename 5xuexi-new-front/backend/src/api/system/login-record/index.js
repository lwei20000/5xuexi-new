import request from '@/utils/request';

/**
 * 分页查询登录日志
 * @param params 查询条件
 */
export async function pageLoginRecords(params) {
  const res = await request.get('/system/login-record/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询登录日志列表
 * @param params 查询条件
 */
export async function listLoginRecords(params) {
  const res = await request.get('/system/login-record', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导出数据
 * @param params 查询条件
 */
export async function doDownloadData(params) {
  const res = await request.get('/system/login-record/dataExport',{
    responseType: 'blob',
    params
  });
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

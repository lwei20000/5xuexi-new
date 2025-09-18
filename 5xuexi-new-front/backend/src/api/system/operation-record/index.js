import request from '@/utils/request';

/**
 * 分页查询操作日志
 * @param params 查询条件
 */
export async function pageOperationRecords(params) {
  const res = await request.get('/system/operation-record/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询操作日志列表
 * @param params 查询条件
 */
export async function listOperationRecords(params) {
  const res = await request.get('/system/operation-record', {
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
  const res = await request.get('/system/operation-record/dataExport',{
    responseType: 'blob',
    params
  });
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

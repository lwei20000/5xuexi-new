import request from '@/utils/request';

/**
 * 管理端-分页查询Remark
 * @param params 查询条件
 */
export async function pageRemarks(params) {
  const res = await request.get('/report/orgRemark/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-添加remark
 * @param data Remark信息
 */
export async function addRemark(data) {
  const res = await request.post('/report/orgRemark/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 管理端-添加remark
 * @param data Remark信息
 */
export async function generateRemark(params) {
  const res = await request.post('/report/orgRemark/generate', null,{
    params
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 管理端-修改Remark
 * @param data remark信息
 */
export async function updateRemark(data) {
  const res = await request.post('/report/orgRemark/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 管理端-删除remark
 * @param id Remarkid
 */
export async function removeRemarks(data) {
  const res = await request.post('/report/orgRemark/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}



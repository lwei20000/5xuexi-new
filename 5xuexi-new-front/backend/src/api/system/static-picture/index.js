import request from '@/utils/request';

/**
 * 分页查询静态图片
 * @param params 查询条件
 */
export async function pageStaticPictures(params) {
  const res = await request.get('/system/sysStaticPicture/page', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加静态图片
 * @param data 静态图片信息
 */
export async function addStaticPicture(data) {
  const res = await request.post('/system/sysStaticPicture/save', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改静态图片
 * @param data 静态图片信息
 */
export async function updateStaticPicture(data) {
  const res = await request.post('/system/sysStaticPicture/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除静态图片
 * @param ids 静态图片id集合
 */
export async function removeStaticPictures(data) {
  const res = await request.post('/system/sysStaticPicture/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

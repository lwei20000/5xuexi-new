import request from '@/utils/request';


/**
 * 获取token
 * @param data 获取token
 */
  export async function getQiniuToken(cvtFlag) {
  const res = await request.get('/qiniu/getToken?cvtFlag='+(cvtFlag?1:0));
  if (res.data.code === 0) {
    return res;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 上传回调
 * @param data 上传回调
 */
export async function qiniuCallBack(data) {
  const res = await request.post('/qiniu/callBack', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}




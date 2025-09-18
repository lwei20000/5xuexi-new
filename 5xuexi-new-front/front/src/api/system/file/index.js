import request from '@/utils/request';

/**
 * 上传文件
 * @param file 文件
 */
export async function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/file/upload', formData);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

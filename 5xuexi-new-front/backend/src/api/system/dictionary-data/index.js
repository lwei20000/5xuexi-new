import request from '@/utils/request';

/**
 * 分页查询字典数据
 * @param params 查询条件
 */
export async function pageDictionaryData(params) {
  const res = await request.get('/system/dictionary-data/page', {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.list && data.list.length>0){
      for(var i=0;i<data.list.length;i++){
        data.list[i].name =  data.list[i].dictName+"_"+ data.list[i].dictDataName;
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

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

/**
 * 添加字典数据
 * @param data 字典数据信息
 */
export async function addDictionaryData(data) {
  const res = await request.post('/system/dictionary-data', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改字典数据
 * @param data 字典数据信息
 */
export async function updateDictionaryData(data) {
  const res = await request.post('/system/dictionary-data/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除字典数据
 * @param id 字典数据id
 */
export async function removeDictionaryData(id) {
  const res = await request.post('/system/dictionary-data/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除字典数据
 * @param data 字典数据id集合
 */
export async function removeDictionaryDataBatch(data) {
  const res = await request.post('/system/dictionary-data/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

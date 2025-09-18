import request from '@/utils/request';

/**
 * 分页查询专业
 * @param params 查询条件
 */
export async function pageMajors(params) {
  const res = await request.get('/base/major/page', {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.list && data.list.length>0){
      for(var i=0;i<data.list.length;i++){
        data.list[i].name =  data.list[i].majorYear+"_" +data.list[i].majorCode  + '_' + data.list[i].majorName+ '_' + data.list[i].majorType+ '_' + data.list[i].majorGradation+ '_' + data.list[i].majorForms+ '_' + data.list[i].majorLength+"年制";
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询专业列表
 * @param params 查询条件
 */
export async function listMajors(params) {
  const res = await request.get('/base/major', {
    params
  });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加专业
 * @param data 专业信息
 */
export async function addMajor(data) {
  const res = await request.post('/base/major', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 复制专业
 * @param data 专业信息
 */
export async function copyMajor(data) {
  const res = await request.post('/base/major/copy', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改专业
 * @param data 专业信息
 */
export async function updateMajor(data) {
  const res = await request.post('/base/major/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除专业
 * @param id 专业id
 */
export async function removeMajor(id) {
  const res = await request.post('/base/major/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除专业
 * @param ids 专业id集合
 */
export async function removeMajors(data) {
  const res = await request.post('/base/major/batch', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 导入租户
 * @param file excel文件
 */
export async function importExcel(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/base/major/import', formData);
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }else {
    return res.data;
  }
}

/**
 * 下载导入模板
 */
export async function downloadTemplate() {
  const res = await request.get('/base/major/templateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

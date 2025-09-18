import request from '@/utils/request';
/**
 * 分页查询租户用户
 * @param params 查询条件
 */
export async function pageUsers(params) {
  const res = await request.get("/system/user/page", {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.list && data.list.length>0){
      for(var i=0;i<data.list.length;i++){
        data.list[i].name =  data.list[i].realname+"_"+ data.list[i].username;
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询租户用户列表
 * @param params 查询条件
 */
export async function listUsers(params) {
  const res = await request.get('/system/user', {
    params
  });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 根据id查询租户用户
 * @param id 用户id
 */
export async function getUser(id) {
  const res = await request.get('/system/user/' + id);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加租户用户
 * @param data 用户租户信息
 */
export async function addUser(data) {
  const res = await request.post('/system/user', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改租户用户
 * @param data 用户信息
 */
export async function updateUser(data) {
  const res = await request.post('/system/user/update', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除用户
 * @param id 用户id
 */
export async function removeUser(id) {
  const res = await request.post('/system/user/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 批量删除用户
 * @param data 用户id集合
 */
export async function removeUsers(data) {
  const res = await request.post('/system/user/batch',data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改用户状态
 * @param userId 用户id
 * @param status 状态
 */
export async function updateUserStatus(userId, status) {
  const res = await request.post('/system/user/status', {
    userId,
    status
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 重置用户密码
 * @param userId 用户id
 * @param password 密码
 * @returns {Promise<string>}
 */
export async function updateUserPassword(userId, password = '123456') {
  const res = await request.post('/system/user/password', {
    userId,
    password
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 导入用户
 * @param file excel文件
 */
export async function importExcel(file) {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post('/system/user/import', formData);
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
  const res = await request.get('/system/user/templateExport',{responseType: 'blob'});
  if (res.status === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 分页查询用户
 * @param params 查询条件
 */
export async function pageAllUsers(params) {
  const res = await request.get("/system/user/admin_page", {
    params
  });
  if (res.data.code === 0) {
    var data = res.data.data;
    if(data && data.list && data.list.length>0){
      for(var i=0;i<data.list.length;i++){
        data.list[i].name =  data.list[i].realname+"_"+ data.list[i].username;
      }
    }
    return data;
  }
  return Promise.reject(new Error(res.data.message));
}

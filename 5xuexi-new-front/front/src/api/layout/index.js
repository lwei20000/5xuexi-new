import request from '@/utils/request';
import {PROJECT_TYPE} from "@/config/setting";

/**
 * 获取当前登录的用户信息、菜单、权限、角色
 */
export async function getUserInfo() {
  const res = await request.get('/auth/user?appType='+PROJECT_TYPE);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取退出当前登录
 */
export async function _logout() {
  const res = await request.get('/logout');
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改当前登录的用户密码
 */
export async function updatePassword(data) {
  const res = await request.post('/auth/password', data);
  if (res.data.code === 0) {
    return res.data.message ?? '修改成功';
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询未读消息
 */
export async function getUnreadNotice() {
  const res = await request.get('/system/announcement/getUnreadNotice');
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

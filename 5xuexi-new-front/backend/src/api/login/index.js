import request from '@/utils/request';
import { setToken } from '@/utils/token-util';

/**
 * 登录
 */
export async function login(data) {
  const res = await request.post('/login', data);
  if (res.data.code === 0) {
    setToken(res.data.data.access_token, data.remember);
  }
  return res.data
}

/**
 * Cas登录
 */
export async function casLogin(params) {
  const res = await request.get('/integrate/uaap/cas/exchange-token',{params});
  if (res.data.code === 0) {
    setToken(res.data.data.access_token, params.remember);
  }
  return res.data
}

/**
 * Oauth2发起登录
 */
export async function toOauth2Login(params) {
  const res = await request.get('/integrate/uaap/oauth2/authorize-url', {params});
  if (res.data.code === 1) {
    return Promise.reject(new Error(res.data.message));
  }
  return res.data
}

/**
 * Oauth2登录
 */
export async function oauth2Login(params) {
  const res = await request.get('/integrate/uaap/oauth2/login', {params});
  if (res.data.code === 0) {
    setToken(res.data.data.access_token, params.remember);
  }
  return res.data
}


/**
 * 切换租户
 */
export async function switchTenant(params) {
  const res = await request.get('/switchTenant',{params:{
      tenantId:params.tenantId
    }});
  if (res.data.code === 0) {
    setToken(res.data.data.access_token, params.remember);
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取验证码
 */
export async function getCaptcha() {
  const res = await request.get('/captcha');
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
/**
 * 获取用户名错误次数
 */
export async function getErrNum(username) {
  const res = await request.get('/err_num?username='+username);
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}


/**
 * 发送邮箱/手机验证码
 * @param data
 * @returns {Promise<*>}
 */
export async function sendCaptcha(data) {
  const res = await request.post('/send_captcha', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 找回密码
 * @param data
 * @returns {Promise<*>}
 */
export async function modifPassword(data) {
  const res = await request.post('/modif_password', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 安全问题找回密码
 * @param data
 * @returns {Promise<*>}
 */
export async function problemPassword(data) {
  const res = await request.post('/problem_password', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

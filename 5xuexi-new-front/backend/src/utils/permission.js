/**
 * 按钮级权限控制
 */
import store from '@/store';

/* 数组是否有某些值 */
const arrayHas = function (array, value) {
  if (!value) {
    return true;
  }
  if (!array) {
    return false;
  }
  if (Array.isArray(value)) {
    for (let i = 0; i < value.length; i++) {
      if (array.indexOf(value[i]) === -1) {
        return false;
      }
    }
    return true;
  }
  return array.indexOf(value) !== -1;
};

/* 数组是否有任意值 */
const arrayHasAny = function (array, value) {
  if (!value) {
    return true;
  }
  if (!array) {
    return false;
  }
  if (Array.isArray(value)) {
    for (let i = 0; i < value.length; i++) {
      if (array.indexOf(value[i]) !== -1) {
        return true;
      }
    }
    return false;
  }
  return array.indexOf(value) !== -1;
};

/**
 * 是否有某些角色
 * @param value 角色字符或字符数组
 */
export function hasRole(value) {
  return arrayHas(store.state.user?.roles, value);
}

/**
 * 是否有任意角色
 * @param value 角色字符或字符数组
 */
export function hasAnyRole(value) {
  return arrayHasAny(store.state.user?.roles, value);
}

/**
 * 是否有某些权限
 * @param value 权限字符或字符数组
 */
export function hasPermission(value) {
  return arrayHas(store.state.user?.authorities, value);
}

/**
 * 是否有任意权限
 * @param value 权限字符或字符数组
 */
export function hasAnyPermission(value) {
  return arrayHasAny(store.state.user?.authorities, value);
}

export default {
  install(Vue) {
    // 添加全局方法
    Vue.prototype.$hasRole = hasRole;
    Vue.prototype.$hasAnyRole = hasAnyRole;
    Vue.prototype.$hasPermission = hasPermission;
    Vue.prototype.$hasAnyPermission = hasAnyPermission;

    // 添加自定义指令
    Vue.directive('role', {
      inserted: (el, binding) => {
        if (!hasRole(binding.value)) {
          el.parentNode && el.parentNode.removeChild(el);
        }
      }
    });
    Vue.directive('any-role', {
      inserted: (el, binding) => {
        if (!hasAnyRole(binding.value)) {
          el.parentNode && el.parentNode.removeChild(el);
        }
      }
    });
    Vue.directive('permission', {
      inserted: (el, binding) => {
        if (!hasPermission(binding.value)) {
          el.parentNode && el.parentNode.removeChild(el);
        }
      }
    });
    Vue.directive('any-permission', {
      inserted: (el, binding) => {
        if (!hasAnyPermission(binding.value)) {
          el.parentNode && el.parentNode.removeChild(el);
        }
      }
    });
  }
};

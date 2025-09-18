import { routeI18nKey, findTabByPath } from 'ele-admin/es/ele-pro-layout/util';
import { PROJECT_NAME, REDIRECT_PATH, I18N_ENABLE } from '@/config/setting';

/**
 * 修改浏览器标题
 * @param title 标题
 */
export function setDocumentTitle(title) {
  const names = [];
  if (title) {
    names.push(title);
  }
  if (PROJECT_NAME) {
    names.push(PROJECT_NAME);
  }
  document.title = names.join(' - ');
}

/**
 * 路由切换更新浏览器标题
 * @param route 路由信息
 * @param $t i18n
 */
export function updateDocumentTitle(route, $t, tabs) {
  const { path, meta, fullPath } = route;
  if (path.includes(REDIRECT_PATH)) {
    return;
  }
  const pathKey = routeI18nKey(path);
  if (!pathKey) {
    return;
  }
  const tab = findTabByPath(fullPath, tabs);
  const title = tab?.title || meta?.title;
  if (!I18N_ENABLE) {
    setDocumentTitle(title);
    return;
  }
  const k = `route.${pathKey}._name`;
  const v = $t(k);
  setDocumentTitle(v === k || !v ? title : v);
}

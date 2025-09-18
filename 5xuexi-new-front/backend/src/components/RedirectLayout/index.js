/** 用于刷新的路由组件 */
import { setRouteReload } from '@/utils/page-tab-util';

export default {
  name: 'RedirectLayout',
  created() {
    const { params, query } = this.$route;
    const from = Array.isArray(params.path)
      ? params.path.join('/')
      : params.path;
    const path = '/' + from;
    setTimeout(() => {
      setRouteReload(null).then(() => {
        this.$router.replace({ path, query });
      });
    }, 100);
  },
  render(h) {
    return h('div');
  }
};

/**
 * iframe 混入
 */
import store from '@/store';
import { TAB_KEEP_ALIVE } from '@/config/setting';

export const iframeMixins = [
  {
    computed: {
      // 是否开启 keep-alive
      keepAlive() {
        return TAB_KEEP_ALIVE && store?.state?.theme?.showTabs;
      }
    }
  }
];

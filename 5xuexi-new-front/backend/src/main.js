import Vue from 'vue';
import App from './App.vue';
import store from './store';
import router from './router';
import permission from './utils/permission';
import { MAP_KEY, LICENSE_CODE } from '@/config/setting';
import EleAdmin from 'ele-admin';
import VueClipboard from 'vue-clipboard2';
import i18n from './i18n';
import './styles/index.scss';

Vue.config.productionTip = false;

Vue.use(EleAdmin, {
  response: {
    dataName: 'list'
  },
  mapKey: MAP_KEY,
  license: LICENSE_CODE,
  i18n: (key, value) => i18n.t(key, value)
});
Vue.use(permission);
Vue.use(VueClipboard);

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App)
}).$mount('#app');


/**
 * select 下拉框 底部触发指令
 */
Vue.directive('selectLoadMore', {
  bind(el, binding) {
    // 获取element-ui定义好的scroll盒子
    const SELECTWRAP_DOM = el.querySelector(
      '.el-select-dropdown .el-select-dropdown__wrap'
    );
    SELECTWRAP_DOM.addEventListener('scroll', function () {
      if (this.scrollHeight - this.scrollTop < this.clientHeight + 1) {
        binding.value();
      }
    });
  }
});



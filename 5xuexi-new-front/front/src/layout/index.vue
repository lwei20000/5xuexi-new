<template>
  <ele-pro-layout
    :menus="menus"
    :tabs="theme.tabs"
    :collapse="theme.collapse"
    :side-nav-collapse="theme.sideNavCollapse"
    :body-fullscreen="theme.bodyFullscreen"
    :show-tabs="theme.showTabs"
    :show-footer="theme.showFooter"
    :head-style="theme.headStyle"
    :side-style="theme.sideStyle"
    :layout-style="theme.layoutStyle"
    :side-menu-style="theme.sideMenuStyle"
    :tab-style="theme.tabStyle"
    :fixed-header="theme.fixedHeader"
    :fixed-sidebar="theme.fixedSidebar"
    :fixed-body="theme.fixedBody"
    :body-full="theme.bodyFull"
    :logo-auto-size="theme.logoAutoSize"
    :colorful-icon="theme.colorfulIcon"
    :side-unique-open="theme.sideUniqueOpen"
    :style-responsive="theme.styleResponsive"
    :project-name="PROJECT_NAME"
    :hide-footers="HIDE_FOOTERS"
    :hide-sidebars="HIDE_SIDEBARS"
    :repeatable-tabs="REPEATABLE_TABS"
    :home-title="HOME_TITLE || $t('layout.home')"
    :home-path="HOME_PATH"
    :layout-path="LAYOUT_PATH"
    :redirect-path="REDIRECT_PATH"
    :locale="locale"
    :i18n="i18n"
    @update:collapse="updateCollapse"
    @update:side-nav-collapse="updateSideNavCollapse"
    @update:body-fullscreen="updateBodyFullscreen"
    @tab-add="addPageTab"
    @tab-remove="removePageTab"
    @tab-remove-all="removeAllPageTab"
    @tab-remove-left="removeLeftPageTab"
    @tab-remove-right="removeRightPageTab"
    @tab-remove-other="removeOtherPageTab"
    @reload-page="reloadPageTab"
    @logo-click="onLogoClick"
    @screen-size-change="screenSizeChange"
    @set-home-components="setHomeComponents"
    @tab-context-menu="onTabContextMenu"
  >
    <!-- 路由出口 -->
    <router-layout />
    <!-- logo 图标 -->
    <template v-slot:logo>
      <img :src="logo" alt="logo" />
    </template>
    <!-- 顶栏右侧区域 -->
    <template v-slot:right>
      <header-tools :fullscreen="fullscreen" @fullscreen="onFullscreen" />
    </template>
    <!-- 全局页脚 -->
    <template v-slot:footer>
      <page-footer />
    </template>
    <!-- 自定义菜单标题增加徽章、小红点 -->
    <template v-slot:title="{ title, item }">
      <span>{{ title }} </span>
      <div v-if="item.meta && item.meta.badge" class="ele-menu-badge">
        <el-badge :value="item.meta.badge" :type="item.meta.badgeColor" />
      </div>
    </template>
    <template v-slot:top-title="{ title, item }">
      <span>{{ title }} </span>
      <div v-if="item.meta && item.meta.badge" class="ele-menu-badge">
        <el-badge :value="item.meta.badge" :type="item.meta.badgeColor" />
      </div>
    </template>
    <template v-slot:nav-title="{ title, item }">
      <span>{{ title }} </span>
      <div v-if="item.meta && item.meta.badge" class="ele-menu-badge">
        <el-badge :value="item.meta.badge" :type="item.meta.badgeColor" />
      </div>
    </template>
  </ele-pro-layout>
</template>

<script>
  import { mapGetters } from 'vuex';
  import { toggleFullscreen, isFullscreen } from 'ele-admin';
  import RouterLayout from '@/components/RouterLayout/index.vue';
  import HeaderTools from './components/header-tools.vue';
  import PageFooter from './components/page-footer.vue';
  import {
    PROJECT_NAME,
    HIDE_SIDEBARS,
    HIDE_FOOTERS,
    REPEATABLE_TABS,
    HOME_TITLE,
    HOME_PATH,
    LAYOUT_PATH,
    REDIRECT_PATH,
    I18N_ENABLE
  } from '@/config/setting';
  import {
    addPageTab,
    removePageTab,
    removeAllPageTab,
    removeLeftPageTab,
    removeRightPageTab,
    removeOtherPageTab,
    reloadPageTab,
    setHomeComponents
  } from '@/utils/page-tab-util';
  import store from "@/store";
  import {API_BASE_URL} from "@/config/setting";
  export default {
    name: 'EleLayout',
    components: {
      RouterLayout,
      HeaderTools,
      PageFooter
    },
    data() {
      return {
        PROJECT_NAME,
        HIDE_SIDEBARS,
        HIDE_FOOTERS,
        REPEATABLE_TABS,
        HOME_PATH,
        HOME_TITLE,
        LAYOUT_PATH,
        REDIRECT_PATH,
        // 是否全屏
        fullscreen: false,
        logo:API_BASE_URL+'/system/sysStaticPicture/logo',
        staticPage:[]
      };
    },
    created() {
      var value = store.state.user.info?.currentTenantId || '';
      if(value){
        for(var i=0;i<store.state.user.tenants.length;i++){
          var item = store.state.user.tenants[i];
          if(item.tenantId == value){
            if(item.tenantLogo){
              this.logo = item.tenantLogo;
            }
            break;
          }
        }
      }
    },
    computed: {
      // 当前语言
      locale() {
        return this.$i18n.locale;
      },
      // 菜单数据
      menus() {
        let arr = this.staticPage.concat(this.$store.state?.user?.menus||[]);
        let list = [...new Set(arr)];
        if (!list || list.length == 0){
          list = this.staticPage;
        }
        return list;
      },
      // 主题状态
      ...mapGetters(['theme'])
    },
    methods: {
      /* 侧栏折叠切换 */
      updateCollapse(value) {
        this.$store.dispatch('theme/setCollapse', value);
      },
      /* 双侧栏一级折叠切换 */
      updateSideNavCollapse(value) {
        this.$store.dispatch('theme/setSideNavCollapse', value);
      },
      /* 内容区域全屏切换 */
      updateBodyFullscreen(value) {
        this.$store.dispatch('theme/setBodyFullscreen', value);
      },
      /* logo 点击事件 */
      onLogoClick(isHome) {
        isHome || this.$router.push(LAYOUT_PATH);
      },
      /* 监听屏幕尺寸改变 */
      screenSizeChange() {
        this.$store.dispatch('theme/updateScreenSize');
        this.fullscreen = isFullscreen();
      },
      /* 全屏切换 */
      onFullscreen() {
        try {
          this.fullscreen = toggleFullscreen();
        } catch (e) {
          this.$message.error('您的浏览器不支持全屏模式');
        }
      },
      /* 页签右键菜单点击事件 */
      onTabContextMenu({ key, tabKey, item, active }) {
        switch (key) {
          case 'reload': // 刷新
            reloadPageTab({
              isHome: !item,
              fullPath: item?.fullPath ?? tabKey
            });
            break;
          case 'close': // 关闭当前
            removePageTab({
              key: item?.fullPath ?? tabKey,
              active
            });
            break;
          case 'left': // 关闭左侧
            removeLeftPageTab({
              key: tabKey,
              active
            });
            break;
          case 'right': // 关闭右侧
            removeRightPageTab({
              key: tabKey,
              active
            });
            break;
          case 'other': // 关闭其他
            removeOtherPageTab({
              key: tabKey,
              active
            });
            break;
        }
      },
      /* 菜单标题国际化 */
      i18n(_path, key) {
        if (!I18N_ENABLE || !key) {
          return;
        }
        const k = 'route.' + key + '._name';
        const title = this.$t(k);
        if (title !== k) {
          return title;
        }
      },
      //
      addPageTab,
      removePageTab,
      removeAllPageTab,
      removeLeftPageTab,
      removeRightPageTab,
      removeOtherPageTab,
      reloadPageTab,
      setHomeComponents
    }
  };
</script>

<style lang="scss">
  // 侧栏菜单徽章样式，定位在右侧垂直居中并调小尺寸
  .ele-menu-badge {
    position: absolute;
    top: 50%;
    right: 14px;
    line-height: 1;
    margin-top: -9px;
    font-size: 0;

    .el-badge__content {
      height: 18px;
      line-height: 18px;
      border-radius: 9px;
      border: none;
      min-width: 18px;
      padding: 0 4px;
      box-sizing: border-box;
    }
  }

  // 父级菜单标题中右侧多定位一点，避免与箭头重合
  .el-submenu > .el-submenu__title .ele-menu-badge {
    right: 36px;
  }

  // 折叠悬浮中样式调整
  .el-menu--popup {
    .el-submenu > .el-submenu__title .ele-menu-badge {
      right: 20px;
    }
  }

  // 侧栏折叠后样式调整
  .ele-admin-collapse .ele-admin-sidebar-menus > .el-scrollbar {
    & > .el-scrollbar__wrap > .el-scrollbar__view > .el-menu {
      & > .el-menu-item,
      & > .el-submenu > .el-submenu__title {
        .ele-menu-badge {
          top: 2px;
          right: 2px;
          margin: 0;
        }
      }
    }
  }

  // 顶栏菜单标题中样式调整
  .ele-admin-header-nav > .el-scrollbar > .el-scrollbar__wrap {
    & > .el-scrollbar__view > .el-menu {
      & > .el-menu-item,
      & > .el-submenu > .el-submenu__title {
        .ele-menu-badge {
          position: static;
          right: auto;
          top: auto;
          display: inline-block;
          vertical-align: 6px;
          margin: 0 0 0 2px;
        }
      }
    }
  }

  // 双侧栏时一级侧栏菜单中样式调整，定位在右上角
  .ele-admin-sidebar-nav-menu > .el-scrollbar > .el-scrollbar__wrap {
    & > .el-scrollbar__view > .el-menu {
      & > .el-menu-item,
      & > .el-submenu > .el-submenu__title {
        .ele-menu-badge {
          top: 0;
          right: 0;
          margin: 0;
        }
      }
    }
  }

  // 双侧栏时一级侧栏菜单折叠后样式调整
  .ele-admin-nav-collapse .ele-admin-sidebar-nav-menu > .el-scrollbar {
    & > .el-scrollbar__wrap > .el-scrollbar__view > .el-menu {
      & > .el-menu-item,
      & > .el-submenu > .el-submenu__title {
        .ele-menu-badge {
          top: 0;
          right: 0;
        }
      }
    }
  }
</style>

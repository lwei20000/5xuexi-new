<!-- 主题设置抽屉 -->
<template>
  <el-drawer
    size="300px"
    :visible="visible"
    :append-to-body="true"
    :title="$t('layout.setting.title')"
    @update:visible="updateVisible"
  >
    <div
      :class="['ele-setting-wrapper', { 'ele-setting-dark': theme.darkMode }]"
    >
      <!-- 侧栏风格 -->
      <div
        v-if="theme.layoutStyle !== 'top'"
        class="ele-setting-theme ele-text-primary"
      >
        <el-tooltip
          :content="$t('layout.setting.sideStyles.dark')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-side-dark"
            @click="updateSideStyle('dark')"
          >
            <i class="el-icon-check" v-if="theme.sideStyle === 'dark'"></i>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="$t('layout.setting.sideStyles.light')"
          placement="top"
        >
          <div class="ele-bg-base" @click="updateSideStyle('light')">
            <i class="el-icon-check" v-if="theme.sideStyle === 'light'"></i>
          </div>
        </el-tooltip>
      </div>
      <!-- 顶栏风格 -->
      <div class="ele-setting-theme ele-text-primary">
        <el-tooltip
          :content="$t('layout.setting.headStyles.light')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-head-light"
            @click="updateHeadStyle('light')"
          >
            <i class="el-icon-check" v-if="theme.headStyle === 'light'"></i>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="$t('layout.setting.headStyles.dark')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-head-dark"
            @click="updateHeadStyle('dark')"
          >
            <i class="el-icon-check" v-if="theme.headStyle === 'dark'"></i>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="$t('layout.setting.headStyles.primary')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-head-primary"
            @click="updateHeadStyle('primary')"
          >
            <div class="ele-bg-primary"></div>
            <i class="el-icon-check" v-if="theme.headStyle === 'primary'"></i>
          </div>
        </el-tooltip>
      </div>
      <!-- 主题色 -->
      <div class="ele-setting-colors">
        <el-tooltip
          v-for="item in themes"
          :key="item.name"
          :content="$t('layout.setting.colors.' + item.name)"
          placement="top"
        >
          <div
            class="ele-setting-color-item"
            :style="{ 'background-color': item.color || item.value }"
            @click="updateColor(item.value)"
          >
            <i
              class="el-icon-check"
              v-if="item.value ? item.value === theme.color : !theme.color"
            >
            </i>
          </div>
        </el-tooltip>
        <!-- 颜色选择器 -->
        <el-color-picker
          v-model="colorValue"
          :predefine="predefineColors"
          class="ele-setting-color-picker"
          @change="updateColor"
        />
      </div>
      <!-- 暗黑模式 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.darkMode') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.darkMode" @change="updateDarkMode" />
        </div>
      </div>
      <el-divider />
      <!-- 导航布局 -->
      <div class="ele-setting-title ele-text-secondary hidden-xs-only">
        {{ $t('layout.setting.layoutStyle') }}
      </div>
      <div class="ele-setting-theme ele-text-primary hidden-xs-only">
        <el-tooltip
          :content="$t('layout.setting.layoutStyles.side')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-side-dark"
            @click="updateLayoutStyle('side')"
          >
            <i class="el-icon-check" v-if="theme.layoutStyle === 'side'"></i>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="$t('layout.setting.layoutStyles.top')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-head-dark ele-layout-top"
            @click="updateLayoutStyle('top')"
          >
            <i class="el-icon-check" v-if="theme.layoutStyle === 'top'"></i>
          </div>
        </el-tooltip>
        <el-tooltip
          :content="$t('layout.setting.layoutStyles.mix')"
          placement="top"
        >
          <div
            class="ele-bg-base ele-layout-mix"
            @click="updateLayoutStyle('mix')"
          >
            <i class="el-icon-check" v-if="theme.layoutStyle === 'mix'"></i>
          </div>
        </el-tooltip>
      </div>
      <!-- 侧栏菜单布局 -->
      <div
        v-if="theme.layoutStyle !== 'top'"
        class="ele-setting-item hidden-xs-only"
      >
        <div class="setting-item-title">
          {{ $t('layout.setting.sideMenuStyle') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            :value="theme.sideMenuStyle === 'mix'"
            @change="updateSideMenuStyle"
          />
        </div>
      </div>
      <!-- 内容区域定宽 -->
      <div class="ele-setting-item hidden-xs-only">
        <div class="setting-item-title">
          {{ $t('layout.setting.bodyFull') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="!theme.bodyFull" @change="updateBodyFull" />
        </div>
      </div>
      <el-divider class="hidden-xs-only" />
      <div class="ele-setting-title ele-text-secondary">
        {{ $t('layout.setting.other') }}
      </div>
      <!-- 固定主体 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.fixedBody') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.fixedBody" @change="updateFixedBody" />
        </div>
      </div>
      <!-- 固定顶栏 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.fixedHeader') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            :disabled="theme.fixedBody"
            :value="theme.fixedHeader"
            @change="updateFixedHeader"
          />
        </div>
      </div>
      <!-- 固定侧栏 -->
      <div
        v-if="theme.layoutStyle !== 'top'"
        class="ele-setting-item hidden-xs-only"
      >
        <div class="setting-item-title">
          {{ $t('layout.setting.fixedSidebar') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            :disabled="theme.fixedBody"
            :value="theme.fixedSidebar"
            @change="updateFixedSidebar"
          />
        </div>
      </div>
      <!-- logo 置于顶栏 -->
      <div
        v-if="theme.layoutStyle !== 'top'"
        class="ele-setting-item hidden-xs-only"
      >
        <div class="setting-item-title">
          {{ $t('layout.setting.logoAutoSize') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.logoAutoSize" @change="updateLogoAutoSize" />
        </div>
      </div>
      <!-- 移动端响应式 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.styleResponsive') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            :value="theme.styleResponsive"
            @change="updateStyleResponsive"
          />
        </div>
      </div>
      <!-- 侧栏彩色图标 -->
      <div v-if="theme.layoutStyle !== 'top'" class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.colorfulIcon') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.colorfulIcon" @change="updateColorfulIcon" />
        </div>
      </div>
      <!-- 侧栏排他展开 -->
      <div v-if="theme.layoutStyle !== 'top'" class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.sideUniqueOpen') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            :value="theme.sideUniqueOpen"
            @change="updateSideUniqueOpen"
          />
        </div>
      </div>
      <!-- 全局页脚 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.showFooter') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.showFooter" @change="updateShowFooter" />
        </div>
      </div>
      <!-- 色弱模式 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.weakMode') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.weakMode" @change="updateWeakMode" />
        </div>
      </div>
      <!-- 页签 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.showTabs') }}
        </div>
        <div class="setting-item-control">
          <el-switch :value="theme.showTabs" @change="updateShowTabs" />
        </div>
      </div>
      <!-- 页签风格 -->
      <div v-if="theme.showTabs" class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.tabStyle') }}
        </div>
        <div class="setting-item-control" style="width: 90px">
          <el-select
            size="mini"
            :value="theme.tabStyle"
            @change="updateTabStyle"
          >
            <el-option
              :label="$t('layout.setting.tabStyles.default')"
              value="default"
            />
            <el-option
              :label="$t('layout.setting.tabStyles.dot')"
              value="dot"
            />
            <el-option
              :label="$t('layout.setting.tabStyles.card')"
              value="card"
            />
          </el-select>
        </div>
      </div>
      <!-- 切换动画 -->
      <div class="ele-setting-item">
        <div class="setting-item-title">
          {{ $t('layout.setting.transitionName') }}
        </div>
        <div class="setting-item-control" style="width: 110px">
          <el-select
            size="mini"
            :value="theme.transitionName"
            @change="updateTransitionName"
          >
            <el-option
              :label="$t('layout.setting.transitions.slideRight')"
              value="slide-right"
            />
            <el-option
              :label="$t('layout.setting.transitions.slideBottom')"
              value="slide-bottom"
            />
            <el-option
              :label="$t('layout.setting.transitions.zoomIn')"
              value="zoom-in"
            />
            <el-option
              :label="$t('layout.setting.transitions.zoomOut')"
              value="zoom-out"
            />
            <el-option
              :label="$t('layout.setting.transitions.fade')"
              value="fade"
            />
          </el-select>
        </div>
      </div>
      <!-- 提示 -->
      <el-divider />
      <el-alert
        type="warning"
        :closable="false"
        class="ele-alert-border"
        :title="$t('layout.setting.tips')"
      />
      <!-- 重置 -->
      <div class="ele-setting-button-group">
        <el-button
          size="small"
          class="ele-fluid"
          icon="el-icon-refresh-left"
          @click="resetSetting"
        >
          {{ $t('layout.setting.reset') }}
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
  import { mapGetters } from 'vuex';

  export default {
    props: {
      // 是否显示, 支持 .sync 修饰
      visible: Boolean
    },
    data() {
      return {
        // 主题列表
        themes: [
          {
            name: 'default',
            color: '#1890ff'
          },
          {
            name: 'dust',
            value: '#5f80c7'
          },
          {
            name: 'sunset',
            value: '#faad14'
          },
          {
            name: 'volcano',
            value: '#f5686f'
          },
          {
            name: 'purple',
            value: '#9266f9'
          },
          {
            name: 'green',
            value: '#33cc99'
          },
          {
            name: 'geekblue',
            value: '#32a2d4'
          }
        ],
        // 颜色选择器预设颜色
        predefineColors: [
          '#f5222d',
          '#fa541c',
          '#fa8c16',
          '#faad14',
          '#a0d911',
          '#52c41a',
          '#13c2c2',
          '#2f54eb',
          '#722ed1',
          '#eb2f96'
        ],
        // 颜色选择器选中颜色
        colorValue: void 0
      };
    },
    computed: {
      ...mapGetters(['theme'])
    },
    created() {
      this.initColorValue();
    },
    methods: {
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
      updateShowTabs(value) {
        this.$store.dispatch('theme/setShowTabs', value);
      },
      updateShowFooter(value) {
        this.$store.dispatch('theme/setShowFooter', value);
      },
      updateHeadStyle(value) {
        this.$store.dispatch('theme/setHeadStyle', value);
      },
      updateSideStyle(value) {
        this.$store.dispatch('theme/setSideStyle', value);
      },
      updateLayoutStyle(value) {
        this.$store.dispatch('theme/setLayoutStyle', value);
      },
      updateSideMenuStyle(value) {
        this.$store.dispatch(
          'theme/setSideMenuStyle',
          value ? 'mix' : 'default'
        );
      },
      updateTabStyle(value) {
        this.$store.dispatch('theme/setTabStyle', value);
      },
      updateTransitionName(value) {
        this.$store.dispatch('theme/setTransitionName', value);
      },
      updateFixedHeader(value) {
        this.$store.dispatch('theme/setFixedHeader', value);
      },
      updateFixedSidebar(value) {
        this.$store.dispatch('theme/setFixedSidebar', value);
      },
      updateFixedBody(value) {
        this.$store.dispatch('theme/setFixedBody', value);
      },
      updateBodyFull(value) {
        this.$store.dispatch('theme/setBodyFull', !value);
      },
      updateLogoAutoSize(value) {
        this.$store.dispatch('theme/setLogoAutoSize', value);
      },
      updateStyleResponsive(value) {
        this.$store.dispatch('theme/setStyleResponsive', value);
      },
      updateColorfulIcon(value) {
        this.$store.dispatch('theme/setColorfulIcon', value);
      },
      updateSideUniqueOpen(value) {
        this.$store.dispatch('theme/setSideUniqueOpen', value);
      },
      updateWeakMode(value) {
        this.$store.dispatch('theme/setWeakMode', value);
      },
      updateDarkMode(value) {
        this.doWithLoading(() =>
          this.$store.dispatch('theme/setDarkMode', value)
        );
      },
      updateColor(value) {
        this.doWithLoading(() => this.$store.dispatch('theme/setColor', value));
      },
      resetSetting() {
        this.doWithLoading(() => this.$store.dispatch('theme/resetSetting'));
      },
      doWithLoading(func) {
        const loading = this.$messageLoading('正在加载主题..');
        setTimeout(() => {
          func()
            .then(() => {
              loading.close();
              this.initColorValue();
            })
            .catch((e) => {
              loading.close();
              console.error(e);
              this.$message.error('主题加载失败');
            });
        }, 0);
      },
      initColorValue() {
        const color = this.theme.color;
        if (color && !this.themes.some((t) => t.value === color)) {
          this.colorValue = color;
        } else {
          this.colorValue = void 0;
        }
      }
    }
  };
</script>

<style lang="scss">
  .ele-setting-wrapper {
    padding: 20px 18px;

    .ele-setting-title {
      font-size: 13px;
      margin-bottom: 15px;
    }

    /* 主题风格 */
    .ele-setting-theme > div {
      width: 52px;
      height: 36px;
      line-height: 1;
      font-size: 18px;
      border-radius: 3px;
      margin: 0 20px 30px 0;
      padding: 14px 0 0 24px;
      box-sizing: border-box;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
      display: inline-block;
      vertical-align: top;
      position: relative;
      overflow: hidden;
      cursor: pointer;
      transition: background-color 0.2s;

      &:before,
      &:after,
      & > .ele-bg-primary {
        content: '';
        width: 100%;
        height: 10px;
        background: #fff;
        position: absolute;
        left: 0;
        top: 0;
        transition: background-color 0.2s;
      }

      &:after {
        width: 14px;
        height: 100%;
      }

      &.ele-side-dark:after,
      &.ele-head-dark:before,
      &.ele-layout-mix:before,
      &.ele-layout-mix:after {
        background: #001529;
      }

      &.ele-head-light:before,
      &.ele-head-dark:before,
      & > .ele-bg-primary {
        z-index: 1;
      }

      &.ele-layout-top {
        padding-left: 19px;

        &:after {
          display: none;
        }
      }
    }

    /* 主题色选择 */
    .ele-setting-colors {
      color: #fff;
      margin-bottom: 20px;

      .ele-setting-color-item {
        width: 20px;
        height: 20px;
        line-height: 22px;
        border-radius: 2px;
        margin: 8px 8px 0 0;
        display: inline-block;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
        vertical-align: top;
        position: relative;
        text-align: center;
        cursor: pointer;
      }
    }

    /* 主题配置项 */
    .ele-setting-item {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      .setting-item-title {
        flex: 1;
        line-height: 28px;
      }

      .setting-item-control {
        line-height: 1;
      }
    }

    .el-divider {
      margin-bottom: 20px;
    }

    .el-alert + .ele-setting-button-group {
      margin-top: 15px;
    }
  }

  /* 适配暗黑模式 */
  .ele-setting-dark .ele-setting-theme > div {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.55);

    &:before,
    &:after,
    & > .ele-bg-primary {
      background: #1f1f1f;
    }

    &.ele-side-dark:after,
    &.ele-head-dark:before,
    &.ele-layout-mix:before,
    &.ele-layout-mix:after {
      background: #262626;
    }
  }

  /* 颜色选择器 */
  .ele-setting-color-picker.el-color-picker {
    height: auto;
    margin-top: 8px;

    .el-color-picker__trigger {
      padding: 0;
      width: 20px;
      height: 20px;
      border: none;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
    }

    .el-color-picker__color {
      border: none;
    }

    .el-color-picker__color-inner {
      border-radius: 2px;
    }

    .el-color-picker__empty {
      background: conic-gradient(
        from 90deg at 50% 50%,
        rgb(255, 0, 0) -19.41deg,
        rgb(255, 0, 0) 18.76deg,
        rgb(255, 138, 0) 59.32deg,
        rgb(255, 230, 0) 99.87deg,
        rgb(20, 255, 0) 141.65deg,
        rgb(0, 163, 255) 177.72deg,
        rgb(5, 0, 255) 220.23deg,
        rgb(173, 0, 255) 260.13deg,
        rgb(255, 0, 199) 300.69deg,
        rgb(255, 0, 0) 340.59deg,
        rgb(255, 0, 0) 378.76deg
      );
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      transform: none;
      border-radius: 2px;
      font-size: 0;
    }

    .el-color-picker__icon {
      font-size: 14px;
    }
  }
</style>

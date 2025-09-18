<!-- 国际化语言切换组件 -->
<template>
  <el-dropdown :placement="placement" @command="changeLanguage" trigger="click">
    <div class="ele-admin-header-tool-item">
      <i class="el-icon-_language" :style="iconStyle"></i>
    </div>
    <template v-slot:dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="en">
          <span :class="{ 'ele-text-primary': language === 'en' }">
            English
          </span>
        </el-dropdown-item>
        <el-dropdown-item command="zh_CN">
          <span :class="{ 'ele-text-primary': language === 'zh_CN' }">
            简体中文
          </span>
        </el-dropdown-item>
        <el-dropdown-item command="zh_TW">
          <span :class="{ 'ele-text-primary': language === 'zh_TW' }">
            繁體中文
          </span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script>
  import { I18N_CACHE_NAME } from '@/config/setting';

  export default {
    props: {
      // dropdown placement
      placement: {
        type: String,
        default: 'bottom'
      },
      // 自定义样式
      iconStyle: Object
    },
    computed: {
      // 当前显示语言
      language() {
        return this.$i18n.locale;
      }
    },
    methods: {
      /* 切换语言 */
      changeLanguage(key) {
        this.$i18n.locale = key;
        localStorage.setItem(I18N_CACHE_NAME, key);
      }
    }
  };
</script>

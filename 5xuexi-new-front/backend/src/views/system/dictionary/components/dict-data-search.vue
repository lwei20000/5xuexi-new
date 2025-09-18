<!-- 搜索表单 -->
<template>
  <el-form
    size="small"
    class="ele-form-search"
    @keyup.enter.native="search"
    @submit.native.prevent
  >
    <el-row :gutter="10">
      <el-col v-bind="styleResponsive ? { md: 8 } : { span: 8 }">
        <el-form-item>
          <el-input
            clearable
            size="small"
            v-model="where.keywords"
            placeholder="输入关键字搜索"
          />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { md: 16 } : { span: 16 }">
        <el-form-item>
          <el-button
            size="small"
            type="primary"
            icon="el-icon-search"
            class="ele-btn-icon"
            @click="search"
          >
            查询
          </el-button>
          <slot></slot>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  export default {
    data() {
      // 默认表单数据
      const defaultWhere = {
        keywords: ''
      };
      return {
        defaultWhere,
        // 表单数据
        where: { ...defaultWhere }
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 搜索 */
      search() {
        this.$emit('search', this.where);
      },
      /*  重置 */
      reset() {
        this.where = { ...this.defaultWhere };
        this.search();
      }
    }
  };
</script>

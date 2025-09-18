<template>
  <div style="max-width: 200px">
    <el-input
      clearable
      size="small"
      v-model="where.keywords"
      placeholder="输入关键字搜索"
      prefix-icon="el-icon-search"
      @change="search"
    />
  </div>
</template>

<script>
  export default {
    data() {
      return {
        // 搜索表单
        where: {
          keywords: ''
        }
      };
    },
    methods: {
      // 搜索
      search() {
        this.$emit('search', this.where);
      }
    }
  };
</script>

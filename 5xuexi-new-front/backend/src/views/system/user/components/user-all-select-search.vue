<template>
  <div style="max-width: 200px">
    <el-input
      clearable
      size="small"
      v-model="where.username"
      placeholder="输入用户名搜索"
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
          username: ''
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

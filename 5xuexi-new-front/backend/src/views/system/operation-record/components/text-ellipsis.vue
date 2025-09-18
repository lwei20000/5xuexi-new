<!-- 文本超出隐藏 -->
<template>
  <div
    :class="[
      'demo-text-ellipsis ele-bg-white ele-border-lighter',
      { expanded: expanded }
    ]"
  >
    <div>{{ content }}</div>
    <div
      class="demo-text-ellipsis-footer ele-border-lighter ele-bg-white"
      @click="expanded = !expanded"
    >
      <i :class="expanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      content: String
    },
    data() {
      return {
        expanded: false
      };
    }
  };
</script>

<style lang="scss" scoped>
  .demo-text-ellipsis {
    border-radius: 4px;
    padding: 6px 12px 20px 12px;
    position: relative;
    border-width: 1px;
    border-style: solid;
    word-break: break-all;

    &:not(.expanded) {
      max-height: 128px;
      overflow: hidden;
    }
  }

  .demo-text-ellipsis-footer {
    border-top-width: 1px;
    border-top-style: solid;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    line-height: 1.3;
    text-align: center;
    font-size: 14px;
    cursor: pointer;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
  }
</style>

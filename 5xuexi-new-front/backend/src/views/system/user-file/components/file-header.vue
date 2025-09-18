<!-- 文件目录面包屑 -->
<template>
  <div class="ele-file-breadcrumb-group ele-cell">
    <div class="ele-cell-content ele-cell">
      <div
        v-if="directorys.length"
        class="ele-file-breadcrumb-back ele-text-primary"
        @click="goBack"
      >
        返回上一级
      </div>
      <div class="ele-file-breadcrumb-list ele-cell-content ele-cell">
        <div
          :class="[
            'ele-file-breadcrumb-item ele-cell',
            { 'ele-text-primary': directorys.length }
          ]"
          @click="goRoot"
        >
          <div class="ele-file-breadcrumb-item-title">全部文件</div>
          <i
            v-if="directorys.length"
            class="el-icon-arrow-right ele-text-secondary"
          ></i>
        </div>
        <div
          v-for="(item, i) in directorys"
          :key="item.id"
          :class="[
            'ele-file-breadcrumb-item ele-cell',
            { 'ele-text-primary': i !== directorys.length - 1 }
          ]"
          @click="goDirectory(i)"
        >
          <div class="ele-file-breadcrumb-item-title">{{ item.name }}</div>
          <i
            v-if="i !== directorys.length - 1"
            class="el-icon-arrow-right ele-text-secondary"
          ></i>
        </div>
      </div>
    </div>
    <div class="hidden-xs-only">已全部加载, 共{{ total }}个</div>
  </div>
</template>

<script>
  export default {
    props: {
      // 文件夹数据
      directorys: Array,
      // 总文件数
      total: Number
    },
    methods: {
      /* 回到上级 */
      goBack() {
        this.$emit(
          'update:directorys',
          this.directorys.slice(0, this.directorys.length - 1)
        );
      },
      /* 回到根目录 */
      goRoot() {
        if (this.directorys.length) {
          this.$emit('update:directorys', []);
        }
      },
      /* 回到指定目录 */
      goDirectory(index) {
        if (index !== this.directorys.length - 1) {
          this.$emit('update:directorys', this.directorys.slice(0, index + 1));
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  /* 文件目录面包屑 */
  .ele-file-breadcrumb-group {
    font-size: 13px;
    padding: 2px 0 6px 0;
    line-height: 1;
  }

  .ele-file-breadcrumb-back {
    padding-right: 10px;
    border-right: 2px solid hsla(0, 0%, 60%, 0.2);

    &:hover {
      text-decoration: underline;
      cursor: pointer;
    }
  }

  .ele-file-breadcrumb-item.ele-text-primary:hover {
    & > .ele-file-breadcrumb-item-title {
      text-decoration: underline;
      cursor: pointer;
    }
  }
</style>

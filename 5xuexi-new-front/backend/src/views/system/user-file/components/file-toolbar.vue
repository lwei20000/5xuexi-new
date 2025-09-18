<template>
  <ele-toolbar theme="none">
    <el-upload
      action=""
      :show-file-list="false"
      :before-upload="onUpload"
      class="ele-action ele-inline-block"
    >
      <el-button
        size="small"
        type="primary"
        class="ele-btn-icon"
        icon="el-icon-upload2"
      >
        上传
      </el-button>
    </el-upload>
    <el-button
      size="small"
      class="ele-btn-icon"
      icon="el-icon-folder-add"
      @click="openFolderAdd"
    >
      新建文件夹
    </el-button>
    <el-button
      size="small"
      type="danger"
      icon="el-icon-delete"
      class="ele-btn-icon hidden-xs-only"
      :disabled="!checked.length"
      @click="removeBatch"
    >
      删除
    </el-button>
    <template v-slot:action>
      <!-- 搜索框 -->
      <div
        :class="['ele-file-tool-search', { 'hidden-xs-only': styleResponsive }]"
      >
        <el-input size="small" v-model="search" placeholder="搜索您的文件">
          <template v-slot:append>
            <el-button icon="el-icon-search"  :loading="loading" @click="doSearch" />
          </template>
        </el-input>
      </div>
      <!-- 显示方式切换 -->
      <i
        v-if="grid"
        class="ele-file-tool-btn el-icon-_menu"
        @click="toggleShowType"
      ></i>
      <i
        v-else
        class="ele-file-tool-btn el-icon-_nav"
        @click="toggleShowType"
      ></i>
    </template>
    <!-- 新建文件夹弹窗 -->
    <folder-add
      :visible.sync="folderAddVisible"
      :parent-id="parentId"
      @done="onDone"
    />
  </ele-toolbar>
</template>

<script>
  import { uploadFile } from '@/api/system/file';
  import { addUserFile, removeUserFiles } from '@/api/system/user-file';
  import FolderAdd from './folder-add.vue';

  export default {
    components: { FolderAdd },
    props: {
      // 是否网格展示
      grid: Boolean,
      // 选中数据
      checked: Array,
      // 父级 id
      parentId: Number
    },
    data() {
      return {
        loading:false,
        // 搜索关键字
        search: '',
        // 新建文件夹弹窗是否打开
        folderAddVisible: false
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      doSearch(){
        this.$emit('done',this.search);
      },
      /* 上传 */
      onUpload(file) {
        if (file.size / 1024 / 1024 > 100) {
          this.$message.error('大小不能超过 100MB');
          return false;
        }
        const loading = this.$messageLoading('上传中..');
        uploadFile(file)
          .then((data) => {
            addUserFile({
              name: data.name,
              isDirectory: 0,
              parentId: this.parentId,
              path: data.path,
              length: data.length,
              contentType: data.contentType,
              uploadType: data.uploadType
            })
              .then(() => {
                loading.close();
                this.$message.success('上传成功');
                this.onDone();
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          })
          .catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        return false;
      },
      /* 打开新建文件夹弹窗 */
      openFolderAdd() {
        this.folderAddVisible = true;
      },
      /* 批量删除 */
      removeBatch() {
        this.$confirm('确定要删除选中的文件吗?', '提示',{ type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$messageLoading('请求中..');
            removeUserFiles(this.checked.map((d) => d.id))
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.onDone();
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          })
          .catch(() => {});
      },
      /* 完成刷新列表数据 */
      onDone() {
        this.$emit('done');
      },
      /* 显示方式切换 */
      toggleShowType() {
        this.$emit('update:grid', !this.grid);
      }
    }
  };
</script>

<style lang="scss" scoped>
  /* 图标按钮 */
  .ele-file-tool-btn {
    cursor: pointer;
    font-size: 22px;
    display: inline-block;
    vertical-align: middle;
    margin-left: 10px;
  }

  /* 搜索框 */
  .ele-file-tool-search {
    display: inline-block;
    vertical-align: middle;
    background-color: hsla(0, 0%, 60%, 0.15);
    border-radius: 32px;

    :deep(.el-input-group__append),
    :deep(.el-input__inner) {
      background-color: transparent;
      border: none;
    }

    :deep(.el-input-group__append) {
      padding: 0;
    }

    :deep(.el-input-group__append .el-button) {
      margin: 0;
      padding: 7px 16px 7px 10px;
      border: none;
    }

    :deep(.el-input-group__append .el-button > i) {
      font-size: 16px;
      font-weight: 600;
    }

    :deep(.el-input__inner) {
      width: 200px;
      padding-right: 0;
      box-sizing: border-box;
    }
  }
</style>

<!-- 文件展示列表 -->
<template>
  <div class="demo-file-list-group">
    <ele-file-list
      v-if="data.length"
      :data="data"
      :grid="grid"
      :sort="sort"
      :order="order"
      :icons="icons"
      :smIcons="smIcons"
      :checked="checked"
      :style="{ minHeight: '76vh', minWidth: grid ? 'auto' : '456px' }"
      @item-click="onItemClick"
      @sort-change="onSortChange"
      @update:checked="updateChecked"
      @item-context-menu="onCtxMenuClick"
    >
      <template v-slot:context-menu="{ item }">
        <el-dropdown-item command="open">打开</el-dropdown-item>
        <el-dropdown-item
          v-if="!item.isDirectory"
          command="download"
          icon="el-icon-download"
          divided
        >
          下载
        </el-dropdown-item>
        <el-dropdown-item
          command="edit"
          icon="el-icon-edit"
          :divided="item.isDirectory"
        >
          重命名
        </el-dropdown-item>
        <el-dropdown-item command="move" icon="el-icon-rank" divided>
          移动到
        </el-dropdown-item>
        <el-dropdown-item command="remove" icon="el-icon-delete">
          删除
        </el-dropdown-item>
      </template>
    </ele-file-list>
    <div v-else style="min-height: 76vh " class="demo-file-list-empty">
      <ele-empty />
    </div>
    <!-- 用于图片预览 -->
    <div style="display: none">
      <el-image
        ref="previewRef"
        v-if="previewOption.visible"
        :src="previewOption.current"
        :preview-src-list="previewImages"
      />
    </div>
    <!-- 文件重命名弹窗 -->
    <name-edit
      :visible.sync="nameEditVisible"
      :data="nameEditData"
      @done="onDone"
    />
    <!-- 文件移动弹窗 -->
    <move-edit
      :visible.sync="moveVisible"
      :data="moveData"
      @done="onDone"
    />
  </div>
</template>

<script>
  import EleFileList from 'ele-admin/es/ele-file-list';
  import NameEdit from './name-edit.vue';
  import MoveEdit from './move-edit.vue';
  import { removeUserFile } from '@/api/system/user-file';
  import {downloadUrl} from "@/api/common";
  import { icons, smIcons } from "./icons";

  export default {
    components: { EleFileList, NameEdit,MoveEdit },
    props: {
      // 父级 id
      parentId: Number,
      // 文件列表数据
      data: Array,
      // 排序字段
      sort: String,
      // 排序方式
      order: String,
      // 选中数据
      checked: Array,
      // 是否网格展示
      grid: Boolean
    },
    data() {
      return {
        // 网格图标
        icons:icons,
        // 表格图标展示
        smIcons:smIcons,
        // 图片预览列表
        previewImages: [],
        // 图片预览配置
        previewOption: {
          visible: false,
          current: null
        },
        // 文件重命名弹窗是否打开
        nameEditVisible: false,
        // 文件重命名的数据
        nameEditData: null,
        // 移动文件弹窗是否打开
        moveVisible: false,
        // 移动文件的数据
        moveData: null
      };
    },
    methods: {
      /* 文件列表排序方式改变 */
      onSortChange(option) {
        this.$emit('sort-change', option);
      },
      /* 更新选中数据 */
      updateChecked(value) {
        this.$emit('update:checked', value);
      },
      /* item 点击事件 */
      onItemClick(item) {
        if (item.isDirectory) {
          // 进入文件夹
          this.$emit('go-directory', item);
        } else if (this.isImageFile(item)) {
          // 预览图片文件
          this.previewItemImage(item);
        } else {
          // 选中或取消选中文件
          this.updateChecked(
            this.checked.includes(item)
              ? this.checked.filter((d) => d !== item)
              : [...this.checked, item]
          );
        }
      },
      /* 右键菜单点击事件 */
      onCtxMenuClick({ key, item }) {
        if (key === 'open') {
          // 打开文件
          if (item.isDirectory || this.isImageFile(item)) {
            this.onItemClick(item);
          } else {
            downloadUrl(item.path,item.name);
          }
        } else if (key === 'download') {
          // 下载文件
          if (typeof item.path === 'string') {
            downloadUrl(item.path,item.name);
          }
        } else if (key === 'edit') {
          // 重命名
          this.nameEditData = item;
          this.nameEditVisible = true;
        } else if (key === 'remove') {
          // 删除文件
          this.removeItem(item);
        }else if(key === 'move'){
          this.moveData = item;
          this.moveVisible = true;
        }
      },
      /* 删除 */
      removeItem(item) {
        this.$confirm('确定要删除此文件吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$messageLoading('请求中..');
            removeUserFile(item.id)
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
      /* 判断是否是图片文件 */
      isImageFile(item) {
        return (
          typeof item.contentType === 'string' &&
          item.contentType.startsWith('image/') &&
          item.url
        );
      },
      /* 预览图片文件 */
      previewItemImage(item) {
        this.previewImages = this.data
          .filter((d) => this.isImageFile(d))
          .map((d) => d.url);
        this.previewOption.current = item.url;
        this.previewOption.visible = true;
        this.$nextTick(() => {
          if (this.$refs.previewRef) {
            this.$refs.previewRef.showViewer = true;
          }
        });
      }
    }
  };
</script>

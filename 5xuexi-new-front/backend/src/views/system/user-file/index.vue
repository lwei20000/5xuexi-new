<template>
  <div class="ele-body">
    <el-card shadow="never" body-style="padding: 0;">
      <div style="padding: 16px 16px 12px 16px">
        <file-toolbar
          :grid.sync="grid"
          :checked="checked"
          :parentId="parentId"
          @done="onDone"
        />
        <file-header
          :total="total"
          :directorys="directorys"
          @update:directorys="updateDirectorys"
        />
      </div>
      <div v-loading="loading">
        <file-list
          :grid="grid"
          :data="data"
          :sort="sort"
          :order="order"
          :parentId="parentId"
          :checked.sync="checked"
          @sort-change="onSortChange"
          @go-directory="onGoDirectory"
          @done="onDone"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
  import FileToolbar from './components/file-toolbar.vue';
  import FileHeader from './components/file-header.vue';
  import FileList from './components/file-list.vue';
  import { listUserFiles } from '@/api/system/user-file';
  import {getThumbnail} from "@/api/common";

  export default {
    name: 'ExtensionFile',
    components: { FileToolbar, FileHeader, FileList },
    data() {
      return {
        // 加载状态
        loading: true,
        // 文件列表数据
        data: [],
        // 排序字段
        sort: '',
        // 排序方式
        order: '',
        // 选中数据
        checked: [],
        // 文件夹数据
        directorys: [],
        // 总文件数
        total: 0,
        // 是否网格展示
        grid: true,
        // 父级 id
        parentId: 0,
        // 搜索条件
        keywords:''
      };
    },
    created() {
      this.query();
    },
    methods: {
      /* 查询文件列表 */
      query() {
        this.data = [];
        this.checked = [];
        this.loading = true;
        listUserFiles({
          sort: this.order ? this.sort : '',
          order: this.order,
          parentId: this.parentId,
          keywords: this.keywords
        })
          .then((list) => {
            this.loading = false;
            this.data = list.map((d) => {
              var thumbnail = null;
              if(d.isDirectory != 1 && d.contentType.indexOf("image")==0){
                if(d.uploadType == "fastdfs"){
                  thumbnail = getThumbnail(d.path);
                }else{
                  thumbnail = d.path;
                }
              }
              return Object.assign({ name: d.name }, d, {
                isDirectory: d.isDirectory === 1 ? true : false,
                length: this.formatLength(d.length),
                url: d.path,
                thumbnail: thumbnail
              });
            });
            this.total = list.length;
          })
          .catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      },
      /* 刷新列表数据 */
      onDone(keywords) {
        this.keywords = keywords;
        this.query();
      },
      /* 文件列表排序方式改变 */
      onSortChange(option) {
        this.order = option.order;
        this.sort = option.sort;
        this.query();
      },
      /* 进入文件夹 */
      onGoDirectory(item) {
        this.updateDirectorys([...this.directorys, item]);
      },
      /* 更新文件夹数据 */
      updateDirectorys(values) {
        this.directorys = values;
        this.parentId = this.directorys[this.directorys.length - 1]?.id ?? 0;
        this.query();
      },
      /* 格式化文件大小 */
      formatLength(length) {
        if (length == null) {
          return '-';
        }
        if (length < 1024) {
          return length + 'B';
        } else if (length < 1024 * 1024) {
          return (length / 1024).toFixed(1) + 'KB';
        } else if (length < 1024 * 1024 * 1024) {
          return (length / 1024 / 1024).toFixed(1) + 'M';
        } else {
          return (length / 1024 / 1024 / 1024).toFixed(1) + 'G';
        }
      }
    }
  };
</script>

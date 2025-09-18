<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <file-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemFileTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-upload
            action=""
            :show-file-list="false"
            :before-upload="onUpload"
            class="ele-action ele-inline-block"
          >
            <el-button
              size="small"
              type="primary"
              icon="el-icon-upload2"
              class="ele-btn-icon"
            >
              上传
            </el-button>
          </el-upload>
          <el-button  v-permission="'sys:file:remove:ef2a3d1a4f994ac496d24386e1e3eb1f'"
            size="small"
            type="danger"
            icon="el-icon-delete"
            class="ele-btn-icon"
            @click="removeBatch"
          >
            删除
          </el-button>
        </template>
        <!-- 文件路径列 -->
        <template v-slot:path="{ row }">
          <a :href="row.path" target="_blank">
            {{ row.path }}
          </a>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-popconfirm v-permission="'sys:file:remove:ef2a3d1a4f994ac496d24386e1e3eb1f'"
            class="ele-action"
            title="确定要删除此文件吗？"
            @confirm="remove(row)"
          >
            <template v-slot:reference>
              <el-link type="danger" :underline="false" icon="el-icon-delete">
                删除
              </el-link>
            </template>
          </el-popconfirm>
        </template>
      </ele-pro-table>
    </el-card>
  </div>
</template>

<script>
  import FileSearch from './components/file-search.vue';
  import {
    pageFiles,
    removeFile,
    removeFiles,
    uploadFile
  } from '@/api/system/file';

  export default {
    name: 'SystemFile',
    components: { FileSearch },
    data() {
      return {
        // 表格列配置
        columns: [
          {
            columnKey: 'selection',
            type: 'selection',
            width: 45,
            align: 'center',
            fixed: 'left'
          },
          {
            columnKey: 'index',
            type: 'index',
            width: 45,
            align: 'center',
            showOverflowTooltip: true,
            fixed: 'left'
          },
          {
            prop: 'name',
            label: '文件名称',
            sortable: 'custom',
            showOverflowTooltip: true
          },
          {
            prop: 'path',
            label: '文件路径',
            sortable: 'custom',
            showOverflowTooltip: true,
            slot: 'path'
          },
          {
            prop: 'length',
            label: '文件大小',
            sortable: 'custom',
            showOverflowTooltip: true,
            width: 110,
            formatter: (row) => {
              if (row.length < 1024) {
                return row.length + 'B';
              } else if (row.length < 1024 * 1024) {
                return (row.length / 1024).toFixed(1) + 'KB';
              } else if (row.length < 1024 * 1024 * 1024) {
                return (row.length / 1024 / 1024).toFixed(1) + 'M';
              } else {
                return (row.length / 1024 / 1024 / 1024).toFixed(1) + 'G';
              }
            }
          },
          {
            prop: 'createRealname',
            label: '上传人',
            sortable: 'custom',
            showOverflowTooltip: true,
            width: 110
          },
          {
            prop: 'createTime',
            label: '上传时间',
            sortable: 'custom',
            showOverflowTooltip: true,
            width: 160,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 130,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 表格选中数据
        selection: []
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageFiles({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeFile(row.id)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
            this.reload();
          })
          .catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      },
      /* 批量删除 */
      removeBatch() {
        if (!this.selection.length) {
          this.$message.error('请至少选择一条数据');
          return;
        }
        this.$confirm('确定要删除选中的文件吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeFiles(this.selection.map((d) => d.id))
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.reload();
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          })
          .catch(() => {});
      },
      /* 上传 */
      onUpload(file) {
        if (file.size / 1024 / 1024 > 100) {
          this.$message.error('大小不能超过 100MB');
          return false;
        }
        const loading = this.$loading({ lock: true });
        uploadFile(file)
          .then(() => {
            loading.close();
            this.$message.success('上传成功');
            this.reload();
          })
          .catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        return false;
      }
    }
  };
</script>

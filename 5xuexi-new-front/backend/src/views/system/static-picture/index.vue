<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <picture-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemPictureTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'system:sysStaticPicture:save:160f678169b44160629165b60d16'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'system:sysStaticPicture:remove:69b4164a8662169165b0d6'"
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
        <template v-slot:picture="{ row }">
          <a :href="row.picture" target="_blank">
            {{ row.picture }}
          </a>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'system:sysStaticPicture:update:160f7869b44a160626695b60d16'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>

          <el-popconfirm v-permission="'system:sysStaticPicture:remove:69b4164a8662169165b0d6'"
            class="ele-action"
            title="确定要删除此静态图片吗？"
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
    <!-- 编辑弹窗 -->
    <picture-edit :data="current" :visible.sync="showEdit" @done="reload" />
  </div>
</template>

<script>
  import PictureSearch from './components/picture-search.vue';
  import PictureEdit from './components/picture-edit.vue';
  import store from '@/store';
  import {
    pageStaticPictures,
    removeStaticPictures
  } from '@/api/system/static-picture';

  export default {
    name: 'BaseStaticPicture',
    components: {
      PictureSearch,
      PictureEdit
    },
    data() {
      return {
        currentTenantId : store.state.user.info.currentTenantId,
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
            prop: 'url',
            label: '图片KEY',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'name',
            label: '图片名称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'picture',
            label: '图片地址',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 260,
            slot: 'picture'
          },
          {
            prop: 'updateTime',
            label: '更新时间',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 140,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 表格选中数据
        selection: [],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageStaticPictures({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 显示编辑 */
      openEdit(row) {
        this.current = row;
        this.showEdit = true;
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeStaticPictures([row.id])
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
            this.reload();
          }).catch((e) => {
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
        this.$confirm('确定要删除选中的静态图片吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeStaticPictures(this.selection.map((d) => d.id))
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.reload();
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          }).catch(() => {});
      }
    }
  };
</script>

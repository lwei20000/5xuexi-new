<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <announcement-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="id"
        :datasource="datasource"
        :need-page="true"
        :selection.sync="selection"
        cache-key="systemAnnouncementTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'system:sysOpenAnnouncement:save:a253e30f789b44a253e306295b0da253e3'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'system:sysOpenAnnouncement:delete:a26e39b44a8a26e36295b0da26e3'"
                     size="small"
                     type="danger"
                     icon="el-icon-delete"
                     class="ele-btn-icon"
                     @click="removeBatch"
          >
            删除
          </el-button>
        </template>
        <!-- 状态列 -->
        <template v-slot:topTimestamp="{ row }">
          <el-switch :disabled="!$hasPermission('sys:user:update:f13dbabbd410476193f385a39234da83')"
                     :active-value="1"
                     :inactive-value="0"
                     v-model="row.topTimestamp"
                     @change="editTopTimestamp(row)"
          />
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'system:sysOpenAnnouncement:update:a23e30f789b44aa23e306295b0da23e3'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'system:sysOpenAnnouncement:delete:a26e39b44a8a26e36295b0da26e3'"
                         class="ele-action"
                         title="确定要删除此公告吗？"
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
    <announcement-edit :data="current" :visible.sync="showEdit" @done="reload" />
  </div>
</template>

<script>
  import AnnouncementSearch from './components/announcement-search.vue';
  import AnnouncementEdit from './components/announcement-edit.vue';
  import {removeOpenAnnouncements,pageOpenAnnouncements,updateTopTimestamp} from '@/api/system/open-announcement';

  export default {
    name: 'SystemOpenAnnouncement',
    components: {
      AnnouncementSearch,
      AnnouncementEdit
    },
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
            prop: 'title',
            label: '标题',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'openAnnouncementType',
            label: '通知类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'topTimestamp',
            label: '置顶',
            align: 'center',
            sortable: 'custom',
            width: 80,
            resizable: false,
            slot: 'topTimestamp',
            showOverflowTooltip: true
          },
          {
            prop: 'createTime',
            label: '创建时间',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 180,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 表格选中数据
        selection: []
      };
    },
    methods: {
      editTopTimestamp(row){
        const loading = this.$loading({ lock: true });
        updateTopTimestamp(row.openAnnouncementId, row.topTimestamp)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
          })
          .catch((e) => {
            loading.close();
            row.status = !row.status ? 1 : 0;
            this.$message.error(e.message);
          });
      },
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageOpenAnnouncements({ ...where, ...order, page, limit});
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
      /* 批量删除 */
      removeBatch() {
        if (!this.selection.length) {
          this.$message.error('请至少选择一条数据');
          return;
        }
        this.$confirm('确定要删除选中的公告吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
          removeOpenAnnouncements(this.selection.map((d) => d.openAnnouncementId))
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.reload();
              }).catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          }).catch(() => {});
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeOpenAnnouncements([row.openAnnouncementId])
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
            this.reload();
          }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      },
    }
  };
</script>

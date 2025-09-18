<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <system-announcement-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="id"
        :datasource="datasource"
        :need-page="true"
        :selection.sync="selection"
        cache-key="SyssystemAnnouncementTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'system:sysSystemAnnouncement:save:a253e308f789b44a253e306295b0da253e3'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'system:sysSystemAnnouncement:delete:a26e39b443a8a26e36295b0da26e3'"
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
          <el-switch :disabled="!$hasPermission('system:sysSystemAnnouncement:update:a23e30f789b44aa23e9306295b0da23e3')"
                     :active-value="1"
                     :inactive-value="0"
                     v-model="row.topTimestamp"
                     @change="editTopTimestamp(row)"
          />
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'system:sysSystemAnnouncement:update:a23e30f789b44aa23e9306295b0da23e3'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'system:sysSystemAnnouncement:delete:a26e39b443a8a26e36295b0da26e3'"
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
          <el-link v-permission="'system:sysSystemAnnouncement:list:253e130f789b44a82b06295b0d253e3'"
                   type="primary"
                   :underline="false"
                   icon="el-icon-user"
                   @click="openUser(row)"
          >
            已读信息
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>
    <!-- 编辑弹窗 -->
    <system-announcement-edit :data="current" :visible.sync="showEdit" @done="reload" />
    <!-- 用户信息 -->
    <system-announcement-user :system-announcement-id="current?.systemAnnouncementId" :key="current?.systemAnnouncementId" :visible.sync="showUser" :has-read="hasRead" />
  </div>
</template>

<script>
  import SystemAnnouncementSearch from './components/system-announcement-search.vue';
  import SystemAnnouncementEdit from './components/system-announcement-edit.vue';
  import SystemAnnouncementUser from './components/system-announcement-user.vue';
  import {removeSystemAnnouncements,pageSystemAnnouncements,updateTopTimestamp} from '@/api/system/system-announcement';

  export default {
    name: 'SysSystemAnnouncement',
    components: {
      SystemAnnouncementSearch,
      SystemAnnouncementEdit,
      SystemAnnouncementUser
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
            prop: 'systemAnnouncementType',
            label: '通知类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'readTotal',
            label: '通知数量',
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
            width: 220,
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
        selection: [],
        //用户
        showUser:false,
        //是否已读
        hasRead:undefined
      };
    },
    methods: {
      editTopTimestamp(row){
        const loading = this.$loading({ lock: true });
        updateTopTimestamp(row.systemAnnouncementId, row.topTimestamp)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
          }).catch((e) => {
            loading.close();
            row.status = !row.status ? 1 : 0;
            this.$message.error(e.message);
          });
      },
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageSystemAnnouncements({ ...where, ...order, page, limit});
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      openUser(row){
        this.current = row;
        this.showUser = true;
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
        this.$confirm('确定要删除选中的公告吗?', '提示',{ type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
          removeSystemAnnouncements(this.selection.map((d) => d.systemAnnouncementId))
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
        removeSystemAnnouncements([row.systemAnnouncementId])
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

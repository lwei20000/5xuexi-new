<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <system-message-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="id"
        :datasource="datasource"
        :need-page="true"
        :selection.sync="selection"
        cache-key="systemMessageTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'system:sysSystemMessage:save:a253e30f789b644a253e306295b0da253e3'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'system:sysSystemMessage:delete:a26e39b44a18a26e36295b0da26e3'"
                     size="small"
                     type="danger"
                     icon="el-icon-delete"
                     class="ele-btn-icon"
                     @click="removeBatch"
          >
            删除
          </el-button>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'system:sysSystemMessage:update:a23e30f789b444aa23e306295b0da23e3'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'system:sysSystemMessage:delete:a26e39b44a18a26e36295b0da26e3'"
                         class="ele-action"
                         title="确定要删除此消息吗？"
                         @confirm="remove(row)"
          >
            <template v-slot:reference>
              <el-link type="danger" :underline="false" icon="el-icon-delete">
                删除
              </el-link>
            </template>
          </el-popconfirm>
          <el-link v-permission="'system:sysSystemMessage:list:253e30f789b44a282b06295b0d253e3'"
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
    <system-message-edit :data="current" :visible.sync="showEdit" @done="reload" />
    <!-- 用户信息 -->
    <system-message-user :system-message-id="current?.systemMessageId" :key="current?.systemMessageId" :visible.sync="showUser" :has-read="hasRead" />
  </div>
</template>

<script>
  import SystemMessageSearch from './components/system-message-search.vue';
  import SystemMessageEdit from './components/system-message-edit.vue';
  import SystemMessageUser from './components/system-message-user.vue';
  import {removeSystemMessages,pageSystemMessages} from '@/api/system/system-message';

  export default {
    name: 'SysSystemMessage',
    components: {
      SystemMessageSearch,
      SystemMessageEdit,
      SystemMessageUser
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
            prop: 'readTotal',
            label: '消息数量',
            showOverflowTooltip: true,
            minWidth: 110
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
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageSystemMessages({ ...where, ...order, page, limit});
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
        this.$confirm('确定要删除选中的消息吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
          removeSystemMessages(this.selection.map((d) => d.systemMessageId))
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
        removeSystemMessages([row.systemMessageId])
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

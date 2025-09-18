<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <feedback-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="id"
        :datasource="datasource"
        :need-page="true"
        :selection.sync="selection"
        cache-key="systemFeedbackTable"
      >
        <template v-slot:status="{ row }">
          <el-tag :type="['danger','warning','success'][row.status]" size="mini" :disable-transitions="true">
            {{ ['未解决','待验证','已解决'][row.status] }}
          </el-tag>
        </template>
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'system:sysFeedback:remove:69b4114a8662119115b0d6'"
                     size="small"
                     type="danger"
                     icon="el-icon-delete"
                     class="ele-btn-icon"
                     @click="removeBatch"
          >
            批量删除
          </el-button>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'system:sysFeedback:update:110f7869b44a110626695b60d11'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            回复
          </el-link>
          <el-popconfirm v-permission="'system:sysFeedback:remove:69b4114a8662119115b0d6'"
                         class="ele-action"
                         title="确定要删除此意见反馈吗？"
                         @confirm="remove(row)">
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
    <feedback-edit :data="current" :visible.sync="showEdit" @done="reload" />
  </div>
</template>

<script>
  import {removeFeedbacks,pageFeedbacks} from '@/api/system/feedback';
  import FeedbackEdit from "@/views/system/feedback/components/feedback-edit.vue";
  import FeedbackSearch from "@/views/system/feedback/components/feedback-search.vue";

  export default {
    name: 'SystemFeedback',
    components: {
      FeedbackSearch,
      FeedbackEdit
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
            prop: 'userId',
            label: '用户',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 100,
            formatter: (_row) => {
              var userStr = "";
              if(_row.user){
                userStr=_row.user.realname +"_"+ _row.user.username;
              }
              return userStr;
            }
          },
          {
            prop: 'title',
            label: '标题',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'phone',
            label: '手机号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 100
          },
          {
            prop: 'time',
            label: '反馈时间',
            showOverflowTooltip: true,
            minWidth: 100,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            prop: 'status',
            label: '状态',
            align: 'center',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 100,
            slot: 'status'
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
        selection: []
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageFeedbacks({ ...where, ...order, page, limit});
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
        this.$confirm('确定要删除选中的意见反馈吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
          removeFeedbacks(this.selection.map((d) => d.id))
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
        removeFeedbacks([row.id])
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

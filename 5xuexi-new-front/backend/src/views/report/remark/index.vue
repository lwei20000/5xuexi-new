<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <remark-search @search="reload" />
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
          <el-button v-permission="'report:orgRemark:save:90f67899b449062995b60d9'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'report:orgRemark:save:90f67899b449062995b60d9'"
                     size="small"
                     type="primary"
                     icon="el-icon-plus"
                     class="ele-btn-icon"
                     @click="batchGenerate"
          >
            批量生成
          </el-button>
          <el-button v-permission="'report:orgRemark:remove:69b494a86629995b0d6'"
                     size="small"
                     type="danger"
                     icon="el-icon-delete"
                     class="ele-btn-icon"
                     @click="removeBatch"
          >
            删除
          </el-button>
        </template>
        <!-- 类型列 -->
        <template v-slot:payFlag="{ row }">
          <el-tag
            :type="['warning','success'][row.payFlag]"
            size="mini"
            :disable-transitions="true"
          >
            {{ ['否','是'][row.payFlag] }}
          </el-tag>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'report:orgRemark:update:90f7869b44a90626695b60d9'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'report:orgRemark:remove:69b494a86629995b0d6'"
                         class="ele-action"
                         title="确定要删除此记录吗？"
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
    <remark-edit :data="current" :visible.sync="showEdit" @done="reload" />
    <!-- 编辑弹窗 -->
    <remark-sync :visible.sync="showBatchGenerate" @done="reload" />
  </div>
</template>

<script>
  import RemarkSearch from './components/remark-search.vue';
  import RemarkEdit from './components/remark-edit.vue';
  import {removeRemarks,pageRemarks} from '@/api/report/remark';
  import RemarkSync from "@/views/report/remark/components/remark-sync";

  export default {
    name: 'ReportOrgRemark',
    components: {
      RemarkSync,
      RemarkSearch,
      RemarkEdit
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
            prop: 'year',
            label: '年级',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'orgId',
            label: '站点',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160,
            formatter: (_row) => {
              var orgStr = "";
              if(_row.org){
                orgStr= _row.org.orgFullName;
              }
              return orgStr;
            }
          },
          {
            prop: 'studentNum',
            label: '学生数量',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'payFlag',
            label: '是否付费',
            align: 'center',
            showOverflowTooltip: true,
            width: 100,
            slot: 'payFlag'
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
        showBatchGenerate:false
      };
    },
    methods: {
      batchGenerate(){
          this.showBatchGenerate = true;
      },
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageRemarks({ ...where, ...order, page, limit});
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
        this.$confirm('确定要删除选中的记录吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
          removeRemarks(this.selection.map((d) => d.id))
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
        removeRemarks([row.id])
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

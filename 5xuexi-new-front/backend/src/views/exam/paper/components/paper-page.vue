<template>
  <div class="ele-body">
    <ele-modal :lock-scroll="false"
      width="90%"
      :visible="visible"
      :centered="true"
      :close-on-click-modal="true"
      custom-class="ele-dialog-form"
      title="试卷列表"
      @update:visible="updateVisible"
    >
      <el-card shadow="never">
        <!-- 数据表格 -->
        <ele-pro-table
          ref="table"
          :columns="columns"
          :datasource="datasource"
          :selection.sync="selection"
          cache-key="systemCourseTable"
        >
          <!-- 表头工具栏 -->
          <template v-slot:toolbar>
            <el-button v-permission="'exam:tPaper:save:60f47869b446062965b40d6'"
                       size="small"
                       type="primary"
                       icon="el-icon-plus"
                       class="ele-btn-icon"
                       @click="openEdit()"
            >
              添加
            </el-button>
            <el-button v-permission="'exam:tPaper:remove:49b464a84626965b0d4'"
                       size="small"
                       type="danger"
                       icon="el-icon-delete"
                       class="ele-btn-icon"
                       @click="removeBatch"
            >
              删除
            </el-button>
          </template>
          <!-- 试卷预览列 -->
          <template v-slot:defaultPaper="{ row }">
            <el-link type="primary" :underline="false" @click="openPreview(row)">
              试卷预览
            </el-link>
          </template>
          <!-- 操作列 -->
          <template v-slot:action="{ row }">
            <el-link
              type="primary"
              :underline="false"
              icon="el-icon-edit"
              @click="openEdit(row)"
            >
              编辑
            </el-link>
            <el-popconfirm v-permission="'exam:tPaper:remove:49b464a84626965b0d4'"
                           class="ele-action"
                           title="确定要删除此试卷吗？"
                           @confirm="remove(row)"
            >
              <template v-slot:reference>
                <el-link type="danger" :underline="false" icon="el-icon-delete">
                  删除
                </el-link>
              </template>
            </el-popconfirm>

            <el-link
              type="primary"
              :underline="false"
              icon="el-icon-download"
              @click="openImport(row)"
            >
              导入答题卡
            </el-link>
          </template>
        </ele-pro-table>
      </el-card>
      <!-- 导入弹窗 -->
      <paper-question-import :paper-id="current?.paperId" :visible.sync="showImport" @done="reload" />

      <paper-edit :course-id="courseId" :data="current" :visible.sync="showEdit" @done="reloadLoad"></paper-edit>

      <paper-preview :paper-id="current?.paperId" v-if="showPaperPreview"  :visible.sync="showPaperPreview"></paper-preview>
    </ele-modal>
  </div>
</template>

<script>
import PaperQuestionImport from './paper-question-import.vue';
import PaperEdit from './paper-edit.vue';
import { pagePapers,removePapers} from "@/api/exam/paper";
import PaperPreview from "@/views/exam/paper/components/paper-preview";
export default {
  name: 'BaseExamPaperList',
  components: {
    PaperQuestionImport,PaperEdit,PaperPreview
  },
  props: {
    // 弹窗是否打开
    visible: Boolean,
    // 课程id
    courseId: Number
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
          prop: 'paperName',
          label: '试卷名称',
          showOverflowTooltip: true,
          minWidth: 200
        },
        {
          prop: 'paperType',
          label: '试卷类型',
          showOverflowTooltip: true,
          minWidth: 80,
          formatter: (_row, _column, cellValue) => {
            return cellValue==1?'文件试卷':'题目试卷';
          }
        },
        {
          prop: 'defaultPaper',
          label: '试卷预览',
          showOverflowTooltip: true,
          minWidth: 80,
          slot: 'defaultPaper'
        },
        {
          prop: 'paperUsage',
          label: '是否默认',
          showOverflowTooltip: true,
          minWidth: 80,
          formatter: (_row, _column, cellValue) => {
            return cellValue==1?'是':'否';
          }
        },
        {
          columnKey: 'action',
          label: '操作',
          width: 230,
          align: 'center',
          resizable: false,
          slot: 'action',
          showOverflowTooltip: true
        }
      ],
      // 表格选中数据
      selection: [],
      // 当前数据
      current: null,
      // 是否显示编辑弹窗
      showEdit: false,
      // 是否显示导入弹窗
      showImport: false,
      showPaperPreview:false
    };
  },
  methods: {
    /* 表格数据源 */
    datasource({ page, limit, where, order }) {
      where.courseId = this.courseId;
      return pagePapers({ ...where, ...order, page, limit });
    },
    reloadLoad(){
      this.$emit('done');
      this.reload();
    },
    /* 刷新表格 */
    reload(where) {
      this.$refs.table.reload({ page: 1, where: where });
    },
    /* 打开导入弹窗 */
    openImport(row) {
      this.current = row;
      this.showImport = true;
    },
    /* 显示试卷列表 */
    openEdit(row) {
      this.current = row;
      this.showEdit = true;
    },
    openPreview(row){
      this.current = row;
      this.showPaperPreview = true;
    },
    /* 更新visible */
    updateVisible(value) {
      this.$emit('update:visible', value);
    },
    /* 删除 */
    remove(row) {
      const loading = this.$loading({ lock: true });
      removePapers([row.paperId])
        .then((msg) => {
          loading.close();
          this.$message.success(msg);
          this.reloadLoad();
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
      this.$confirm('确定要删除选中的试卷吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
          const loading = this.$loading({ lock: true });
          removePapers(this.selection.map((d) => d.paperId))
            .then((msg) => {
              loading.close();
              this.$message.success(msg);
              this.reloadLoad();
            })
            .catch((e) => {
              loading.close();
              this.$message.error(e.message);
            });
        })
        .catch(() => {});
    }
  }
};
</script>

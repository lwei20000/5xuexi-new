<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <major-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemMajorTable"
      >
        <!-- 操作列 -->
        <template v-slot:lastSemester="{ row }">
          <el-link type="primary" v-if="row.lastSemester" @click="showMajorCourseVive(row)">
            {{row.lastSemester? '第'+semesterMap[row.lastSemester]+'学期':'未设置' }}
          </el-link>
        </template>
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button  v-permission="'base:major:save:d6f326b8cbe24752a91e1f73e6cf1057'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button  v-permission="'base:major:save:d6f326b8cbe24752a91e1f73e6cf1057'"
                      size="small"
                      type="success"
                      icon="el-icon-plus"
                      class="ele-btn-icon"
                      @click="openCopy()"
          >
            复制
          </el-button>
          <el-button v-permission="'base:major:save:d6f326b8cbe24752a91e1f73e6cf1057'"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openImport"
          >
            导入
          </el-button>
          <el-button v-permission="'base:major:remove:889a7080e0ab45fbb35847cde5d7ac53'"
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
          <el-link v-permission="'base:major:update:3139f76d-6294441b9bb3b9d771721ccc'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'base:major:remove:889a7080e0ab45fbb35847cde5d7ac53'"
            class="ele-action"
            title="确定要删除此专业吗？"
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
    <major-edit :data="current" :visible.sync="showEdit" @done="reload" />
    <!-- 导入弹窗 -->
    <major-import :visible.sync="showImport" @done="reload" />
    <!-- copy弹窗 -->
    <major-copy :visible.sync="showCopy" @done="reload" />
    <!-- 专业课程 -->
    <MajorCourseModal :visible.sync="showMajorCourse" :major="current" v-if="showMajorCourse" @done="reload" />
  </div>
</template>

<script>
  import MajorSearch from './components/major-search.vue';
  import MajorEdit from './components/major-edit.vue';
  import MajorCopy from './components/major-copy.vue';
  import MajorImport from './components/major-import.vue';
  import MajorCourseModal from './components/major-course-modal.vue';
  import {
    pageMajors,
    removeMajor,
    removeMajors
  } from '@/api/base/major';

  export default {
    name: 'BaseMajor',
    components: {
      MajorSearch,
      MajorImport,
      MajorCopy,
      MajorEdit,
      MajorCourseModal
    },
    data() {
      return {
        semesterMap:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"},
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
            prop: 'majorYear',
            label: '招生年份',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'majorCode',
            label: '专业代码',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'majorName',
            label: '专业名称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 210
          },
          {
            prop: 'majorType',
            label: '教育类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'majorGradation',
            label: '专业层次',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'majorForms',
            label: '学习形式',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'majorLength',
            label: '专业学制',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110,
            formatter: (_row, _column, cellValue) => {
              return cellValue+"年制";
            }
          },
          {
            prop: 'lastSemester',
            label: '最新学期课程',
            showOverflowTooltip: true,
            minWidth: 110,
            slot: 'lastSemester',
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
        selection: [],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 是否显示导入弹窗
        showImport: false,
        // 是否显示复制弹窗
        showCopy: false,
        // 是否显示专业课程
        showMajorCourse: false
      };
    },
    methods: {
      showMajorCourseVive(row){
        this.current = row;
        this.showMajorCourse = true;
      },
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageMajors({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      openCopy(){
        this.showCopy = true;
      },
      /* 打开导入弹窗 */
      openImport() {
        this.showImport = true;
      },
      /* 显示编辑 */
      openEdit(row) {
        this.current = row;
        this.showEdit = true;
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeMajor(row.majorId)
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
        this.$confirm('确定要删除选中的专业吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeMajors(this.selection.map((d) => d.majorId))
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
      }
    }
  };
</script>

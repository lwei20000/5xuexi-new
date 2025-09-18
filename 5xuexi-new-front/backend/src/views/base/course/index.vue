<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <course-search @search="reload" />
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
          <el-button v-permission="'base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'base:course:remove:a0a553c5733544d4b275d68506fe7b40'"
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
          <el-link v-if="$hasPermission('base:course:update:9db31879a48e44c5a3c6cd802172418d') && row.tenantId == currentTenantId"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>

          <el-popconfirm v-if="$hasPermission('base:course:remove:a0a553c5733544d4b275d68506fe7b40') && row.tenantId == currentTenantId"
            class="ele-action"
            title="确定要删除此课程吗？"
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
            icon="el-icon-edit"
            @click="openCourseware(row)"
          >
            章节课时
          </el-link>

          <el-link v-if="$hasPermission('base:course:update:9db31879a48e44c5a3c6cd802172418d')"
                   type="primary"
                   :underline="false"
                   icon="el-icon-edit"
                   @click="openAliasEdit(row)"
          >
            修改别名
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>
    <!-- 编辑弹窗 -->
    <course-edit :data="current" :visible.sync="showEdit" @done="reload" />

    <course-alias-edit :data="current" :visible.sync="showAliasEdit" @done="reload" />

    <CoursewareList ref="courseware" :data="current" :visible.sync="showCoursewareEdit" v-if="showCoursewareEdit"/>
  </div>
</template>

<script>
  import CourseSearch from './components/course-search.vue';
  import CourseEdit from './components/course-edit.vue';
  import CoursewareList from '../courseware/index.vue';
  import store from '@/store';
  import {
    pageCourses,
    removeCourse,
    removeCourses
  } from '@/api/base/course';
  import CourseAliasEdit from "@/views/base/course/components/course-alias-edit.vue";

  export default {
    name: 'BaseCourse',
    components: {
      CourseAliasEdit,
      CourseSearch,
      CourseEdit,
      CoursewareList
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
            fixed: 'left',
            selectable: (row) => {
              return  row.tenantId == this.currentTenantId;
            }
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
            prop: 'courseCode',
            label: '课程代码',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'courseName',
            label: '课程名称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'comments',
            label: '备注',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'updateTime',
            label: '更新时间',
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
            width: 340,
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
        // 是否显示章节课时弹窗
        showCoursewareEdit: false,

        showAliasEdit:false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageCourses({ ...where, ...order, page, limit });
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
      openAliasEdit(row) {
        this.current = row;
        this.showAliasEdit = true;
      },
      openCourseware(row){
        this.current = row;
        this.current.isEdit = row.tenantId == this.currentTenantId;
        this.showCoursewareEdit = true;
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeCourse(row.courseId)
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
        this.$confirm('确定要删除选中的课程吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeCourses(this.selection.map((d) => d.courseId))
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

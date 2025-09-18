<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <major-course-search @search="reload" />
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
          <el-button v-permission="'base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button  v-permission="'base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1'" v-if="!major"
                      size="small"
                      type="success"
                      icon="el-icon-plus"
                      class="ele-btn-icon"
                      @click="openCopy()"
          >
            复制
          </el-button>
          <el-button v-permission="'base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1'" v-if="!major"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openImport"
          >
            导入
          </el-button>
          <el-button v-permission="'base:majorCourse:remove:deb745cf0f484e159bf932bdf14c4dcf'"
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
          <el-link v-permission="'base:majorCourse:update:773ed130545045bdb8dd0ab55cc2e141'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'base:majorCourse:remove:deb745cf0f484e159bf932bdf14c4dcf'"
            class="ele-action"
            title="确定要删除此专业课程吗？"
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
    <major-course-edit :data="current" :visible.sync="showEdit" :major="major" @done="handleEditDone" />
    <!-- 导入弹窗 -->
    <major-course-import :visible.sync="showImport" @done="reload" />
    <!-- copy弹窗 -->
    <major-course-copy :visible.sync="showCopy" @done="reload" />
  </div>
</template>

<script>
  import MajorCourseSearch from './components/major-course-search.vue';
  import MajorCourseEdit from './components/major-course-edit.vue';
  import MajorCourseCopy from './components/major-course-copy.vue';
  import MajorCourseImport from './components/major-course-import.vue';
  import {
    pageMajorCourses,
    removeMajorCourse,
    removeMajorCourses
  } from '@/api/base/majorCourse';

  export default {
    name: 'BaseMajorCourse',
    components: {
      MajorCourseSearch,
      MajorCourseImport,
      MajorCourseCopy,
      MajorCourseEdit
    },
    props: {
      // 专业数据
      major: Object
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
            prop: 'majorId',
            label: '专业',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 310,
            formatter: (_row) => {
              var majorStr = "";
              if(_row.major){
                majorStr=_row.major.majorYear+"_" +_row.major.majorCode+"_" +_row.major.majorName+"_"+_row.major.majorType+"_"+_row.major.majorGradation + "_"+_row.major.majorForms+"_"+_row.major.majorLength+"年制";
              }
              return majorStr;
            },
            show:!this.major
          },
          {
            prop: 'semester',
            label: '学期',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80,
            formatter: (_row, _column, cellValue) => {
              return "第"+this.semesterMap[cellValue]+"学期";
            }
          },
          {
            prop: 'courseId',
            showOverflowTooltip: true,
            minWidth: 200,
            renderHeader: (h) => {
              return h('div', [
                h('span', '课程'),
                h('el-tooltip', {
                  props: {
                    content: '此处显示别名，别名由当前租户自定义',
                    placement: 'top'
                  }
                }, [
                  h('i', {
                    class: 'el-icon-question',
                    style: 'margin-left: 5px; color: #409EFF; cursor: pointer;'
                  })
                ])
              ]);
            },
            formatter: (_row) => {
              var courseStr = "";
              if(_row.course){
                courseStr=_row.course.courseCode+"_"+_row.course.courseName
              }
              return courseStr;
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
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 是否显示导入弹窗
        showImport: false,
        // 是否显示复制弹窗
        showCopy: false
      };
    },
    methods: {

      handleEditDone(data) {
        if (data && data.majorId) {
          this.reload({}, data.majorId);
        } else {
          this.reload();
        }
      },

      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        if(this.major){
          where.majorId = this.major.majorId;
        }
        return pageMajorCourses({ ...where, ...order, page, limit });
      },

      // 在 index.vue 组件中修改 reload 方法
      /* 刷新表格 */
      reload(where, majorId) {
        // 如果传入了 majorId，则使用该 ID 刷新数据
        if (majorId) {
          this.$refs.table.reload({ page: 1, where: { ...where, majorId: majorId } });
        } else {
          this.$refs.table.reload({ page: 1, where: where });
        }
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
        removeMajorCourse(row.id)
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
        this.$confirm('确定要删除选中的专业课程吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeMajorCourses(this.selection.map((d) => d.id))
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

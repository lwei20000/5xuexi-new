<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <user-course-search ref="userCourseSearch" @search="reload" />
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
          <el-button
            size="small"
            type="primary"
            icon="el-icon-download"
            class="ele-btn-icon"
            @click="doDownloadData"
          >
            导出
          </el-button>
          <el-button v-permission="'base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd'"
              size="small"
              type="primary"
              icon="el-icon-plus"
              class="ele-btn-icon"
              @click="doUpdateScore"
          >
            批量修正成绩
          </el-button>
          <el-button v-permission="'base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd'"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openScoreImport"
          >
            导入考试成绩
          </el-button>
        </template>

        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'base:userCourse:update:af11a126b1cf1816a6fcd8135f1cebdd'"
                   type="primary"
                   :underline="false"
                   icon="el-icon-edit"
                   @click="openEdit(row)"
          >
            修改成绩
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>

    <!-- 导入弹窗 -->
    <user-course-score-import :visible.sync="showScoreImport" @done="reload" />
    <user-course-edit :data="current" :visible.sync="showEdit" @done="reload"></user-course-edit>
  </div>
</template>

<script>
  import UserCourseSearch from './components/user-course-search.vue';
  import {
    pageUserCourses, doDownloadData, updateUserCoursesScore
  } from '@/api/base/userCourse';
  import {download} from "@/api/common";
  import UserCourseScoreImport from "@/views/base/userCourse/components/user-course-score-import";
  import UserCourseEdit from "@/views/base/userCourse/components/user-course-edit";

  export default {
    name: 'BaseUserCourse',
    components: {
      UserCourseEdit,
      UserCourseScoreImport,
      UserCourseSearch
    },
    data() {
      return {
        indexers:0,
        semesterMap:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"},
        // 表格列配置
        columns: [
          // {
          //   columnKey: 'selection',
          //   type: 'selection',
          //   width: 45,
          //   align: 'center',
          //   fixed: 'left',
          //   disabled: true // 添加 disabled 属性，使其不可选
          // },
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
            label: '学生',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160,
            formatter: (_row) => {
              var userStr = "";
              if(_row.user){
                userStr=_row.user.realname +"_"+ _row.user.username;
              }
              return userStr;
            }
          },
          {
            prop: 'userNumber',
            label: '学号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160,
            formatter: (_row) => {
              var userStr = "";
              if(_row.user){
                userStr=_row.userNumber;
              }
              return userStr;
            }
          },
          {
            prop: 'majorId',
            label: '专业',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 210,
            formatter: (_row) => {
              var majorStr = "";
              if(_row.major){
                majorStr=_row.major.majorYear+"_" +_row.major.majorCode+"_"+
                  _row.major.majorName+"_"+_row.major.majorType+"_"+_row.major.majorGradation +
                  "_"+_row.major.majorForms+"_"+_row.major.majorLength+"年制";
              }
              return majorStr;
            }
          },
          {
            prop: 'orgId',
            label: '站点',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 180,
            formatter: (_row) => {
              var orgStr = "";
              if(_row.org){
                orgStr= _row.org.orgFullName;
              }
              return orgStr;
            }
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
            minWidth: 210,
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
            prop: 'learingProgress',
            label: '学习进度',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'learingScore',
            label: '学习成绩',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'examScore',
            label: '考试成绩',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'totalScore',
            label: '总成绩',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 120,
            align: 'center',
            resizable: false,
            slot: 'action'
          }
        ],
        // 表格选中数据
        selection: [],
        showScoreImport:false,
        current:null,
        showEdit:false
      };
    },
    methods: {
      /* 显示编辑 */
      openEdit(row) {
        this.current = row;
        this.showEdit = true;
      },
      openScoreImport() {
        this.showScoreImport = true;
      },
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        if(this.indexers == 0){
          where.academicYear = new Date().getFullYear()+'';
          this.indexers++;
        }
        return pageUserCourses({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      doUpdateScore(){
        this.$confirm('确定要修改当前查询出来的所有学习成绩数据吗?', '确认', { type: 'warning',lockScroll:false })
            .then(() => {
                  this.$confirm('再次确定要修改当前查询出来的所有学习成绩数据吗?', '再次确认', {center:true, type: 'success',lockScroll:false })
                      .then(() => {
                          const loading = this.$loading({ lock: true });
                          this.$refs.table.doRequest(({ where , filterValue }) => {
                            where  = this.$refs.userCourseSearch.where;
                            updateUserCoursesScore({ ...where,...filterValue}).then((msg) => {
                              loading.close();
                              this.$message.success(msg);
                              this.reload()
                            }).catch((e) => {
                              loading.close();
                              this.$message.error(e.message);
                            });
                          });
                      }).catch(() => {});
            }).catch(() => {});
      },
      doDownloadData(){
        const loading = this.$loading({ lock: true });

        this.$refs.table.doRequest(({ where, order , filterValue }) => {
          where  = this.$refs.userCourseSearch.where;
          doDownloadData({ ...where, ...order ,...filterValue}).then((data) => {
            loading.close();
            download(data,"用户学习进度.xlsx");
          }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        });
      },
    }
  };
</script>

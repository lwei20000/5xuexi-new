<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <correct-search ref="correctSearch" @search="reload" />
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
        </template>

        <!-- 状态 -->
        <template v-slot:status="{ row }">
          <el-tag
            :type="['info', 'primary','success','danger'][row.status]"
            size="mini"
            :disable-transitions="true"
          >
            {{ ['答题进行中','提交未批改','提交已批改','未考试'][row.status] }}
          </el-tag>
        </template>

        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openPaper(row)"
          >
            查看/批改
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>



    <paper-correct :user-major-course-exam-id="current?.id" v-if="showPaperCorrect"  :visible.sync="showPaperCorrect" @done="reload"></paper-correct>
  </div>
</template>

<script>
  import CorrectSearch from './components/correct-search.vue';
  import {
    pageUserMajorCourseExams,doDownloadData
  } from '@/api/exam/correct';
  import PaperCorrect from "@/views/exam/correct/components/paper-correct";
  import {download} from "@/api/common";

  export default {
    name: 'BaseUserCourse',
    components: {
      PaperCorrect,
      CorrectSearch
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
            prop: 'score',
            label: '考试成绩',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 80
          },
          {
            prop: 'status',
            label: '状态',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 100,
            slot: 'status'
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 120,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 表格选中数据
        selection: [],

        current: null,

        showPaperCorrect: false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageUserMajorCourseExams({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      openPaper(row){
        this.current = row
        this.showPaperCorrect = true;
      },
      doDownloadData(){
        const loading = this.$loading({ lock: true });

        this.$refs.table.doRequest(({ where, order , filterValue }) => {
          where  = this.$refs.correctSearch.where;
          doDownloadData({ ...where, ...order ,...filterValue}).then((data) => {
            loading.close();
            download(data,"考试信息.xlsx");
          }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        });
      }
    }
  };
</script>

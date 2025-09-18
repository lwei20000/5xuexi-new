<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <publish-paper-search ref="publishPaperSearch" @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemCourseTable"
      >

        <!-- 试卷预览列 -->
        <template v-slot:lastPaper="{ row }">
          <el-link v-if="row.majorCourseExam && row.majorCourseExam.paper" type="primary" :underline="false" @click="openPreview(row.majorCourseExam.paper)">
            {{row.majorCourseExam.paper.paperName}}
          </el-link>
        </template>

        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="batch_save()"
          >
            批量发布
          </el-button>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'base:majorCourse:update:773ed130545045bdb8dd0ab55cc2e141'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openMajorCoursePublishs(row)"
          >
            发布列表
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>

    <major-course-publish :data="queryData" v-if="batchSaveShow" :visible.sync="batchSaveShow" ></major-course-publish>

    <major-course-publish-page :major-course-id="current?.id" :course-id="current?.courseId" v-if="showMajorCoursePublishs" :visible.sync="showMajorCoursePublishs"  @done="reload"> </major-course-publish-page>

    <paper-preview :paper-id="paperId" v-if="showPaperPreview" :visible.sync="showPaperPreview"></paper-preview>
  </div>
</template>

<script>
  import PublishPaperSearch from './components/publish_paper-search';
  import {pagePublishMajorCourses} from "@/api/exam/publish";
  import MajorCoursePublish from "@/views/exam/publish/components/major-course-publish";
  import MajorCoursePublishPage from "@/views/exam/publish/components/major-course-publish-page";
  import PaperPreview from "@/views/exam/paper/components/paper-preview";
  export default {
    name: 'BasePublishPaper',
    components: {
      MajorCoursePublish,
      PublishPaperSearch,
      MajorCoursePublishPage,
      PaperPreview
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
            minWidth: 220,
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
            minWidth: 70,
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
            prop: 'lastPaper',
            label: '考试试卷',
            showOverflowTooltip: true,
            minWidth: 140,
            slot: 'lastPaper'
          },
          {
            prop: 'lastSatrtTime',
            label: '考试开始时间',
            showOverflowTooltip: true,
            minWidth: 120,
            formatter: (_row) => {
              if(_row.majorCourseExam){
                return this.$util.toDateString(_row.majorCourseExam.startTime);
              }else{
                return "";
              }
            }
          },
          {
            prop: 'lastEndPaper',
            label: '考试结束时间',
            showOverflowTooltip: true,
            minWidth: 120,
            formatter: (_row) => {
              if(_row.majorCourseExam){
                return this.$util.toDateString(_row.majorCourseExam.endTime);
              }else{
                return "";
              }
            }
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
        // 当前编辑数据
        current: null,
        // 是否显示列表弹窗
        showMajorCoursePublishs: false,

        batchSaveShow:false,
        //查询参数
        queryData:null,

        showPaperPreview:false,

        paperId:null
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pagePublishMajorCourses({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 批量发布 */
      batch_save() {
        this.$refs.table.doRequest(({ where ,filterValue}) => {
          where = this.$refs.publishPaperSearch.where;
          this.queryData = {...where,...filterValue};
        });
        if(!this.queryData || !this.queryData.academicYear){
          this.$message.error('需要选择一个学年进行批量发布');
          return;
        }
        this.batchSaveShow=true;
      },
      openMajorCoursePublishs(row){
        this.current = row;
        this.showMajorCoursePublishs=true;
      },
      openPreview(paper){
        this.paperId = paper.paperId;
        this.showPaperPreview = true;
      }
    }
  };
</script>

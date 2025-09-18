<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <paper-search @search="reload" />
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

        </template>
        <!-- 试卷预览列 -->
        <template v-slot:defaultPaper="{ row }">
          <el-link v-if="row.paperId" type="primary" :underline="false" @click="openPreview(row)">
            试卷预览
          </el-link>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openPaper(row)"
          >
            试卷列表
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>

   <paper-page :course-id="current?.courseId" v-if="showPapers"  :visible.sync="showPapers" @done="reload"></paper-page>

    <paper-preview :paper-id="current?.paperId" v-if="showPaperPreview" :visible.sync="showPaperPreview"></paper-preview>
  </div>
</template>

<script>
  import PaperSearch from './components/paper-search';
  import PaperPage from './components/paper-page';
  import {pagePaperCourses} from "@/api/exam/paper";
  import PaperPreview from "@/views/exam/paper/components/paper-preview";

  export default {
    name: 'BaseExamPaper',
    components: {
      PaperPreview,
      PaperSearch,PaperPage
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
            prop: 'courseId',
            showOverflowTooltip: true,
            minWidth: 260,
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
            prop: 'paperName',
            label: '默认试卷名称',
            showOverflowTooltip: true,
            minWidth: 200
          },
          {
            prop: 'defaultPaper',
            label: '默认试卷预览',
            showOverflowTooltip: true,
            minWidth: 80,
            slot: 'defaultPaper'
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
        // 当前数据
        current: null,
        // 是否显示试卷弹窗
        showPapers: false,
        showPaperPreview:false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pagePaperCourses({ ...where, ...order, page, limit });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 显示试卷列表 */
      openPaper(row) {
        this.current = row;
        this.showPapers = true;
      },
      openPreview(row){
        this.current = row;
        this.showPaperPreview = true;
      }
    }
  };
</script>

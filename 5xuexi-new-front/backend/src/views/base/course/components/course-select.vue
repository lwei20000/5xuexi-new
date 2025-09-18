<template>
      <ele-table-select
        ref="select"
        v-model="value"
        :multiple="multiple"
        :clearable="clearable"
        :placeholder="placeholder"
        value-key="courseId"
        label-key="name"
        :table-config="tableConfig"
        :popper-width="580"
        :init-value="initValue"
        @change="updateValue"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <course-select-search @search="search" />
        </template>
      </ele-table-select>
</template>

<script>
  import { pageCourses } from '@/api/base/course';
  import CourseSelectSearch from './course-select-search';

  export default {
    props: {
      params:undefined,
      // 回显值
      initValue: undefined,
      //支持多选
      multiple:{default: false},
      //取消按钮
      clearable:{default: true},
      //提示语
      placeholder: {default: "请选择课程"},
    },
    components: { CourseSelectSearch },
    data() {
      return {
        value: undefined,
        // 可搜索表格配置
        tableConfig: {
          datasource: ({ page, limit, where, order }) => {
            return pageCourses({ ...where,...this.params, ...order, page, limit });
          },
          columns: [
            {
              columnKey: 'selection',
              type: 'selection',
              width: 45,
              show:this.multiple,
              align: 'center',
              reserveSelection: true
            },
            {
              columnKey: 'index',
              type: 'index',
              width: 45,
              align: 'center',
              showOverflowTooltip: true
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
            }
          ],
          pageSize: 5,
          pageSizes: [5, 10, 15, 20],
          rowClickChecked: true,
          rowClickCheckedIntelligent: false,
          toolkit: ['reload', 'columns'],
          size: 'small',
          toolStyle: { padding: '0 10px' }
        },
      };
    },
    methods: {
      updateValue(value) {
        this.$emit('input', value);
      },
      // 搜索
      search(where) {
        this.$refs.select.reload({
          where: where,
          page: 1
        });
      }
    }
  };
</script>

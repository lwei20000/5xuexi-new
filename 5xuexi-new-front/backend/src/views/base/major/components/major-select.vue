<template>
      <ele-table-select
        ref="select"
        v-model="value"
        :multiple="multiple"
        :clearable="clearable"
        :placeholder="placeholder"
        value-key="majorId"
        label-key="name"
        :table-config="tableConfig"
        :popper-width="580"
        :init-value="initValue"
        @change="updateValue"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <major-select-search @search="search" />
        </template>
      </ele-table-select>
</template>

<script>
  import { pageMajors} from '@/api/base/major';
  import MajorSelectSearch from './major-select-search';

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
      placeholder: {default: "请选择专业"},
    },
    components: { MajorSelectSearch },
    data() {
      return {
        value: undefined,
        // 可搜索表格配置
        tableConfig: {
          datasource: ({ page, limit, where, order }) => {
              return pageMajors({ ...where,...this.params, ...order, page, limit });
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
              prop: 'majorYear',
              label: '招生年份',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              prop: 'majorCode',
              label: '专业代码',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              prop: 'majorName',
              label: '专业名称',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 110
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
